package program;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created for project: TableExtraction
 * In package: program
 * Created with IntelliJ IDEA.
 * User: Sander van Boom
 * Date: 10-10-13
 * Time: 11:05
 */
public class RestructureUsingOCR {
    private Set<String> purificationHeaders = new HashSet<String>();
    private Set<String> kineticHeaders = new HashSet<String>();
    private Set<String> activityHeaders = new HashSet<String>();
    private Collection<Purification> Pheaders;
    private Collection<Kinetics> Kheaders;
    private Collection<RelativeActivity> Aheaders;
    private ArrayList<String> Headers;

    //TODO: create a method for the detection of whitespace, so more information can be extracted succesfully.

    public RestructureUsingOCR() throws IOException {

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Purification>>(){}.getType();
        Type collectionType2 = new TypeToken<Collection<Kinetics>>(){}.getType();
        Type collectionType3 = new TypeToken<Collection<RelativeActivity>>(){}.getType();

        try{
            Reader reader;
            reader = new InputStreamReader(HeaderMethods.class.getResourceAsStream("/program/purification.json"), "UTF-8");
            this.Pheaders = gson.fromJson(reader, collectionType);
            for(Purification p: Pheaders){
                for(int u =0; u<p.getSynonyms().length;u++){
                    this.purificationHeaders.add(p.getSynonyms()[u]);
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * If you want to run a full OCR and retrieve the patternMatrix that way (and ignore any findings of using pdf2csv.js) run this method.
     */
    public ArrayList<ArrayList<String>> creatOCRMatrix(String fileLocation) throws IOException {
        ArrayList<ArrayList<String>> OCRMatrix = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> locations;
        ArrayList<ArrayList<String>> columnContent = new ArrayList<ArrayList<String>>();
        //First we need to find the headers
        locations = findHeaders(fileLocation);
        //Then we need to extract all the information under the header
        double X1 = 0.0;
        double X2 = 0.0;
        double Y1 = 0.0;
        ArrayList<ArrayList<String>> columnPatterns = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> columnFields = new ArrayList<ArrayList<String>>();
        ArrayList<String> foundHeaders = new ArrayList<String>();
        for(ArrayList<String> location : locations){
            foundHeaders.add(location.get(0));
            X1 = Double.parseDouble(location.get(1));
            X2 = Double.parseDouble(location.get(2));
            Y1 = Double.parseDouble(location.get(3));
            columnContent = columnChecker(X1, X2, Y1, fileLocation);
            ArrayList<String> fields = new ArrayList<String>();
            for(int x = 0; x< columnContent.size();x++){
                if((x%2) == 0){
                    fields.add(columnContent.get(x).get(0));                                                  //TODO : check out if this assumption (every instance is only 1 String) is correct.
                }
            }
            //Then we need to evaluate the header (i.e. add a pattern to the header
            ArrayList<String> columnPattern = new ArrayList<String>();
            columnPattern = evaluateColumn(fields);
            columnPatterns.add(columnPattern);
            columnFields.add(fields);
        }
        //first we need the types of the headers:
        ArrayList<String> headerTypes= new ArrayList<String>();
        headerTypes = findHeaderTypes(foundHeaders);

        System.out.println(foundHeaders);
        //System.out.println(headerTypes);
        //System.out.println(columnFields.get(0));
        //System.out.println(columnFields.get(1));
        //System.out.println(columnFields.get(2));
        //System.out.println(columnFields.get(3));
        //System.out.println(columnFields.get(4));
        //System.out.println(columnFields.get(5));
        //System.out.println(columnPatterns);
        System.out.println(columnFields);       //TODO: change the fields or give the correct headers to the refinedColumnMatrix. Otherwise the merging of headers is useless.
        ArrayList<ArrayList<ArrayList<String>>> refinedColumnMatrix = columnRefining(columnFields, columnPatterns, headerTypes);
        columnFields = new ArrayList<ArrayList<String>>();
        columnPatterns = new ArrayList<ArrayList<String>>();

            columnFields.addAll(refinedColumnMatrix.get(0));
            columnPatterns.addAll(refinedColumnMatrix.get(1));

        System.out.println(columnFields);
        System.out.println(columnPatterns);
        //Then we need to flip the columns in the matrix so we can get the rows (transpose the matrix)
        ArrayList<ArrayList<String>> transposedPattern = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> transposedFields;
        transposedPattern = transpose(columnPatterns);
        transposedFields = transpose(columnFields);
        //Then we need to return the OCR Matrix.
        OCRMatrix = transposedPattern;

        //System.out.println(columnPatterns);
        //System.out.println(transposedFields);
        //System.out.println(OCRMatrix);
        evaluateLines(OCRMatrix, transposedFields);
        return OCRMatrix;
    }

    //TODO: Make sure the program doesn't add the headers if their Y locations is below the detection of the word Table.
    //TODO: Make a filter method to make sure you don't get any rubbish headers that clearly don't belong to the table (POSITIONS!)
    //TODO: Make a method that links 2 words togheter if they become a header.
    //TODO: make a method that creates a list of words in the same line. lines with more then 2 headers are marked as headerlines.

   /*
    * This method will read in the file and tries to find any headers as soon as it discovers a table.
    */
    public ArrayList<ArrayList<String>> findHeaders(String fileLocation) throws IOException {
        ArrayList<ArrayList<String>> locationsOfHeaders = new ArrayList<ArrayList<String>>();

        File input = new File(fileLocation);
        Document doc = Jsoup.parse(input, "UTF-8", "http://example.com/");
        Elements spans = doc.select("span.ocrx_word");

        String[] positions = null;
            for(Element span : spans){
                String word = span.text();
                word = findMergedHeaders(Integer.parseInt(span.attr("ID").replaceAll("\\D", "")), spans, word);
                for(String header:purificationHeaders){
                    if(word.contains(header)){
                        ArrayList<String> locationsOfHeader = new ArrayList<String>();
                        locationsOfHeader.add(header);
                        String pos = span.attr("title");
                        positions = pos.split("\\s+");
                        locationsOfHeader.add(positions[1]);
                        locationsOfHeader.add(positions[3]);
                        locationsOfHeader.add(positions[2]);
                        locationsOfHeader.add(positions[4]);
                        locationsOfHeaders.add(locationsOfHeader);
                    }
                }
            }
        return locationsOfHeaders;
    }
    /*
     * This method checks whetever the found header is actually made up from multiple words.
     */
    public String findMergedHeaders(int wordID, Elements spans, String header) throws IOException {
        String mergedHeader =header;

        String currentID;
        Element previousSpan = null;
        for(Element span : spans){
            currentID = span.attr("ID").replaceAll("\\D", "");
            if(Integer.parseInt(currentID) == wordID){
                try{
                //System.out.println("Trying to merge: "+ span.text() + " with "+ previousSpan.text() );
                if(previousSpan.text().equals("Total")||previousSpan.text().equals("Specific")){
                    //System.out.println("NEED TO MERGE: " + span.text() + " with "+ previousSpan.text());
                    mergedHeader = previousSpan.text() + " "  + span.text();
                    //System.out.println(mergedHeader);
                }}
                catch(NullPointerException e){

                }
            }
            previousSpan = span;
        }
        return mergedHeader;
    }

    /*
     * This method reads all the information that is below the header. In order to do so, it needs the location of the header.
     * It uses the calcDistance method.
     * The final output contains 2 ArrayLists (Always right after each other):
     * 1. An ArrayList with the strings that were found below the header
     * 2. An ArrayList with the positions of the strings that were found below the header
     *
     * So:
     * Strings1         locations1
     * Stringy          some strings
     * things           X1
     *                  Y1                             etc.
     *                  X2
     *                  Y2
     */


    public ArrayList<ArrayList<String>> columnChecker(double X1, double X2, double Y1,String fileLocation) throws IOException {
        ArrayList<ArrayList<String>> columnContent = new ArrayList<ArrayList<String>>();
        File input = new File(fileLocation);
        Document doc = Jsoup.parse(input, "UTF-8", "http://example.com/");

        Elements spans = doc.select("span.ocrx_word");
        String[] positionsS = null;
        for(Element span : spans){
            String pos = span.attr("title");
            positionsS = pos.split("\\s+");
            double doupje =0.0 ;
            double doupje2 =0.0 ;
            String positionsOfContentLine = "";
            for(int i = 0; i<positionsS.length;i++){
                if(positionsS[i].equals("bbox")){
                    //do nothing
                }
                else{
                    Integer I = Integer.parseInt(positionsS[1]);
                    Integer I2 = Integer.parseInt(positionsS[2]);
                    doupje = (I.doubleValue());
                    doupje2 = (I2.doubleValue());
                }
            }
            if(doupje>= X1 && doupje <=(X1+calcDistance(X1, X2))&&doupje2 >= Y1){
                ArrayList<String> columnContentPositions = new ArrayList<String>();
                ArrayList<String> columnContentString = new ArrayList<String>();
                columnContentString.add(span.text());
                positionsOfContentLine = span.attr("title");
                columnContentPositions.add(positionsOfContentLine);
                columnContent.add(columnContentString);
                columnContent.add(columnContentPositions);
            }
        }
        return columnContent;
    }
    /*
     * This method returns the type correlating with the header.
     */
    public ArrayList<String> findHeaderTypes(ArrayList<String> headers){
        ArrayList<String> headerTypes = new ArrayList<String>();
        for(String headerInput : headers){
            for(Purification p : Pheaders){
                ArrayList<String> syns = new ArrayList<String>();
                String synType = "";
                for(int i = 0;i<p.getSynonyms().length;i++){
                    syns.add(p.getSynonyms()[i]);
                    synType = p.getTypes()[0];
                }
                if(syns.contains(headerInput)){
                    headerTypes.add(synType);
                }
            }
        }
        return headerTypes;
    }

    /*
     * This method will link the information of the header to the units below the header. Deleting all the words that
     * Don't match the headers type. If more then 5 words are deleted before a match then the column is deleted.
     */

    //TODO: Make sure we dont lose any information containing the content of the headers.

    public ArrayList<ArrayList<ArrayList<String>>> columnRefining(ArrayList<ArrayList<String>> columnsMatrix, ArrayList<ArrayList<String>> patternMatrix, ArrayList<String> headerTypes){
        ArrayList<ArrayList<ArrayList<String>>> refinedColumnsMatrix = new ArrayList<ArrayList<ArrayList<String>>>();
        ArrayList<ArrayList<String>> refinedColumnsContentMatrix = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> refinedColumnsPatternMatrix = new ArrayList<ArrayList<String>>();
        ArrayList<String> refinedHeaderTypes = new ArrayList<String>();
        String type ="";
        ArrayList<String> currentColumnContent = new ArrayList<String>();
        ArrayList<String> currentColumnPattern = new ArrayList<String>();

        //System.out.println(columnsMatrix);

        for(int o =0;o<headerTypes.size();o++){
            currentColumnContent = columnsMatrix.get(o);
            currentColumnPattern = patternMatrix.get(o);
            ArrayList<String> newColumnContent = new ArrayList<String>();
            ArrayList<String> newColumnPattern = new ArrayList<String>();
            type = headerTypes.get(o);
            int breaker = 0;
            boolean stopReading = false;
            for(int u = 0; u<currentColumnPattern.size();u++){
                //System.out.println(!currentColumnPattern.get(u).equals(type));
                if(!currentColumnPattern.get(u).equals(type)||stopReading){
                    breaker++;
                    currentColumnPattern.remove(u);
                    currentColumnContent.remove(u);
                }
                else if(!stopReading){
                    //System.out.println(currentColumnPattern.get(u));
                    newColumnContent.add(currentColumnContent.get(u));
                    newColumnPattern.add(currentColumnPattern.get(u));
                }
                if(breaker>=5){
                    stopReading = true;
                }
            }
            if(newColumnContent.isEmpty()){
             //do nothing, discard the column.
            }
            else{
            refinedColumnsContentMatrix.add(newColumnContent);
            refinedColumnsPatternMatrix.add(newColumnPattern);
            }
        }
        //System.out.println(refinedColumnsContentMatrix);
        //System.out.println(refinedColumnsPatternMatrix);
        refinedColumnsMatrix.add(refinedColumnsContentMatrix);
        refinedColumnsMatrix.add(refinedColumnsPatternMatrix);
        //System.out.println(refinedColumnsMatrix);
        return refinedColumnsMatrix;
    }
    /*
     * This method transposes the matrix (i.e. changes colums to rows and rows to colums)
     */
    public ArrayList<ArrayList<String>> transposeMatrix(ArrayList<ArrayList<String>> matrix){
        ArrayList<ArrayList<String>> transposedMatrix = new ArrayList<ArrayList<String>>();

        //String field = "";
        for(ArrayList<String> line : matrix){
            ArrayList<String> transposedLine = new ArrayList<String>();

        }

        return transposedMatrix;
    }
    /*
     * This method transposes a Matrix containing Strings.
     */
    public ArrayList<ArrayList<String>> transpose(ArrayList<ArrayList<String>> matrix) {
        ArrayList<ArrayList<String>> trans = new ArrayList<ArrayList<String>>();
        int N = matrix.get(0).size();
        for (int i = 0; i < N; i++) {
            ArrayList<String> col = new ArrayList<String>();
            for (ArrayList<String> row : matrix) {
                try{
                    col.add(row.get(i));
                }
                catch(IndexOutOfBoundsException e){
                    return trans;
                }

            }
            trans.add(col);
        }
        return trans;
    }

    /*
     * This method is similar to the RestructureUsingNLP method, evalueSentence.
     * However in OCR we read in columns (derived from the headers) and now in lines.
     *
     */
    //TODO: evaluateColumn needs to take the information of the header type into account as well.
    public ArrayList<String> evaluateColumn(ArrayList<String> column){
        ArrayList<String> pattern = new ArrayList();
            try{
                for(int x = 0;x<column.size();x++){

                    try{
                        Double.parseDouble(column.get(x));
                        pattern.add("N");}
                    catch(NumberFormatException nme)
                    {

                        if(isNumber(column.get(x))){
                            pattern.add("N");
                        }
                        else{
                            //System.out.println(line[x]);
                            if(column.get(x).equals("ND")||column.get(x).equals("|")||column.get(x).contains(">")||column.get(x).contains("<")
                                    ||column.get(x).contains("N/D")||(column.get(x).contains("±")&&isNumber(column.get(x+1)))
                                    ||(column.get(x).equals("-") && isNumber(column.get(x)))||column.get(x)=="NA"||column.get(x)=="/"){
                                pattern.add("N");
                            }else{
                                pattern.add("S");
                            }
                        }
                    }
                }
            }
            catch (NullPointerException e){

            }

        return pattern;
    }
    /*
     * Evaluate line is a more generic method for the checking the patterns in rows. Ideally this works on both the csv extractor
     * and the OCR methods. It is trained on OCR data.
     */
    public ArrayList evaluateLines(ArrayList<ArrayList<String>> lines, ArrayList<ArrayList<String>> content){
        ArrayList<String> pattern = new ArrayList<String>();
        ArrayList<String> lastLine = new ArrayList<String>();
        int counter = 0;
        for (ArrayList<String> line : lines){
            if(line.equals(lastLine)){
                System.out.println("PATTERN: " + line + lastLine);
                System.out.println(content.get(counter-1));
                System.out.println(content.get(counter));

            }
            lastLine = line;
            counter++;
        }
        return pattern;
    }

        public double calcDistance(double x1, double x2){
            double distance = 0.0;
            if(x1 > x2){
                distance = x1 - x2;
            }
            else if(x2 > x1){
                distance = x2 - x1;
            }
            return distance;
        }

    public static boolean isNumber(String string) {
        //Pattern pattern = Pattern.compile("^(\\d+.*|-\\d+.*)");
        Pattern pattern = Pattern.compile("^(\\d+.*|-\\d+.*)");
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
        //return string.matches("^\\d+$");
    }
    }


