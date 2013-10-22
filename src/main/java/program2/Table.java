package program2;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import program.HeaderMethods;
import program.Purification;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.*;

/*
 * Created for project: TableExtraction
 * In package: program2
 * Created with IntelliJ IDEA.
 * User: Sander van Boom
 * Date: 15-10-13
 * Time: 16:02
 */

/*
 * The table class contains methods and properties of the table.
 * You can call getters and setters to acces the various properties of a Table.
 * It also contains methods calling it's subclass Column to change it's attributes.
 */
public class Table {
    private String fileLocation;
    private int LDDistance;
    private ArrayList<String> tableLocations;             //Contains both the found tableKeywords and the locations.
    private String name;
    private Elements spans;
    private CommonMethods CM;
    private LevenshteinDistance LD;
    private double lowestY;                               //lowestY is the begining of the table.
    private String X1ofCurrentWord;
    private double startOfData;

    private Set<String> purificationHeaders = new HashSet<String>();
    private Collection<Purification> Pheaders;
    private ArrayList<ArrayList<String>> locationsOfHeaders;
    private ArrayList<Column> columns;
    private Map <Integer, ArrayList<String>> tableMap;
    private ArrayList<Integer> X2ColumnBoundaries = new ArrayList<Integer>();
    private ArrayList<Integer> X1ColumnBoundaries = new ArrayList<Integer>();
    private int endOfTable;
    private int beginOfTable;

    public Table(int LDDistance, Elements spans) throws IOException {
        System.out.println("Table Created.");
        this.name = spans.get(0).text() + " "+spans.get(2).text() +" " + spans.get(3).text()+ " " + spans.get(4).text() + " " + spans.get(5).text();
        System.out.println("My name is: " + name);

        this.LDDistance = LDDistance;
        this.spans = spans;
        CommonMethods CM = new CommonMethods();
        this.CM = CM;
        LevenshteinDistance LD = new LevenshteinDistance();
        this.LD = new LevenshteinDistance();

        String pos = spans.get(0).attr("title");
        String[] positions = pos.split("\\s+");

        lowestY = Double.parseDouble(positions[2]);
        //~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-

        //TODO: Support the other tableTypes as well.

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Purification>>(){}.getType();

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

        //~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-
        findColumns();
        createColumns();
        refineColumnsByPattern();
        System.out.println("Now we recreate the table using the positions. This might add more data!");

        Scores score = new Scores(spans);
        this.endOfTable = score.findEndOfTable();
        this.beginOfTable = score.findBeginOfTable();

        System.out.println("Begin of the table at " + beginOfTable);
        System.out.println("End of the table at: " + endOfTable);
        createTableMap(endOfTable);                                               //restructure using the tablemap.

        checkMapForX2Columns();

        //reFindColumns();
        printNewColumns();
        //refineColumnsByPattern();

    }

    //In case you have your own TableLocations (note that it is stored in: [keyword, X1, X2, Y1, Y2] You can set the variable here.
    public void setTableLocations(ArrayList<String> newTableLocations){
        this.tableLocations = newTableLocations;

    }

    //Returns the found table locations.
    public ArrayList<String> getTableLocations(){
        return tableLocations;
    }

    /*
    * Every table has columns. But the program needs to find them first.
    *
    */
    public ArrayList<ArrayList<String>> findColumns() throws IOException {
        String[] positions = null;
        int distance =0;
        ArrayList<ArrayList<String>> locationsOfHeaders = new ArrayList<ArrayList<String>>();

        for(Element span : spans){
            String word = span.text();
            String pos = span.attr("title");
            positions = pos.split("\\s+");
            X1ofCurrentWord = positions[1];

            word = findMergedHeaders(Integer.parseInt(span.attr("ID").replaceAll("\\D", "")), spans, word);
            for(String header:purificationHeaders){
                if(word.contains(header)){
                    ArrayList<String> locationsOfHeader = new ArrayList<String>();
                    locationsOfHeader.add(header);
                    if(Double.parseDouble(positions[2]) > lowestY){
                        //System.out.println("FOUND"+word);
                        locationsOfHeader.add(X1ofCurrentWord);
                        locationsOfHeader.add(positions[3]);
                        locationsOfHeader.add(positions[2]);
                        locationsOfHeader.add(positions[4]);
                        locationsOfHeaders.add(locationsOfHeader);
                        break;
                    }
                }
                else {
                    distance = LD.computeLevenshteinDistance(word, header);
                    if(distance < LDDistance){
                        //System.out.println("FOUND"+word);
                        ArrayList<String> locationsOfHeader = new ArrayList<String>();
                        locationsOfHeader.add(header);

                        if(Double.parseDouble(positions[2]) > lowestY){
                            locationsOfHeader.add(X1ofCurrentWord);
                            locationsOfHeader.add(positions[3]);
                            locationsOfHeader.add(positions[2]);
                            locationsOfHeader.add(positions[4]);
                            locationsOfHeaders.add(locationsOfHeader);
                        }
                    }
                }
            }
        }
        //Now we have a list of the location of Headers. The next step is to use the information to create objects.
        this.locationsOfHeaders = locationsOfHeaders;
        return locationsOfHeaders;
    }

    /*
     * This method checks whetever the found header is actually made up from multiple words.
     * It takes the spans, the wordID (currently searched word) and the header as information from the findHeader method.
     */

    //TODO: Add the LevenshteinDistance class in this method so it can use the distance in the prefix. Watch out for false positives.

    public String findMergedHeaders(int wordID, Elements spans, String header) throws IOException {
        String mergedHeader =header; //the merged header equals the current header. This is only changed if there is a prefix found.

        String currentID;
        Element previousSpan = null;
        String[] positions;
        for(Element span : spans){
            currentID = span.attr("ID").replaceAll("\\D", "");
            if(Integer.parseInt(currentID) == wordID){
                try{
                    if(previousSpan.text().equals("Total")||previousSpan.text().equals("Specific")||previousSpan.text().equals("Purification")||previousSpan.text().equals("Puriﬁcation")||previousSpan.text().equals("Speciﬁc")||previousSpan.text().equals("Specific")){
                        mergedHeader = previousSpan.text() + " "  + span.text();
                        String pos = previousSpan.attr("title");
                        positions = pos.split("\\s+");
                        X1ofCurrentWord = positions[1];
                    }}
                catch(NullPointerException e){
                    //e.printStackTrace();
                    //do nothing.
                }
            }
            previousSpan = span;
        }
        return mergedHeader;
    }

    /*
     * This method creates the columns and puts the in the ArrayList.
     */
    public ArrayList<Column> createColumns(){
        this.startOfData = Double.MAX_VALUE;
        ArrayList<Column> columns = new ArrayList<Column>();
        for(ArrayList<String> location : locationsOfHeaders){
            double X1 = 0.0;
            double X2 = 0.0;
            double Y1 = 0.0;
            double Y2 = 0.0;
            String header = "";
            header = location.get(0);
            X1 = Double.parseDouble(location.get(1));
            X2 = Double.parseDouble(location.get(2));
            Y1 = Double.parseDouble(location.get(3));
            Y2 = Double.parseDouble(location.get(4));
            if(Y1 < startOfData){
                this.startOfData = Y1;
            }
            Column col = new Column(header, X1, X2, Y1, spans, Pheaders);
            columns.add(col);
        }
        this.columns = columns;
        return columns;
    }

    /*
     * The RefineColumnsByPattern is a complicated method. It searches trough each column object and extracts the type
     * of the header and the type of the content (cells). Then it matches the type with the content, Anything that doesn't
     * match is being deleted. Note that units etc. are being removed in this process as well.
     * For this reason the content will be saved in a new ArrayList.
     */
    public ArrayList<String> refineColumnsByPattern(){
        ArrayList<String> cols = new ArrayList<String>();
        ArrayList<String> colTypes = new ArrayList<String>();
        for(Column column : columns){
            colTypes = column.evaluateColumn();
           // System.out.println(colTypes);
        }

        return cols;
    }

    public Map<Integer, ArrayList<String>> createTableMap(int endOfTable){
        Map<Integer, ArrayList<String>> tableMap = new HashMap<Integer, ArrayList<String>>();
        //System.out.println(startOfData +" "+" is the start of all data." );
        int counter = 0;
        String[] positions;
        while(counter < 10000){
            ArrayList<String> pixelContent = new ArrayList<String>();
            for(Element span : spans){
                String pos = span.attr("title");
                positions = pos.split("\\s+");
                int x1 = Integer.parseInt(positions[1]);
                int x2 = Integer.parseInt(positions[3]);
                int y1 = Integer.parseInt(positions[2]);
                int y2 = Integer.parseInt(positions[4]);
                if(counter >= x1 && counter <= x2 && y1>=startOfData &&y2<endOfTable){
                    //System.out.println(span.text());
                    pixelContent.add(span.text());
                }
                else if(pixelContent.isEmpty()){
                    //tableMap.put(counter, emptyList);
                }
            }
            tableMap.put(counter, pixelContent);
            counter++;
        }
        //System.out.println(tableMap.get(600) + " should not be empty.");
        this.tableMap = tableMap;
        return tableMap;
    }

    //Dont use this method just jet. We need to fix the columnscoring first.
    //TODO: What this method needs is the end of the table!!! For this it also needs to know the end of the row!!!
    public ArrayList<Integer> checkMapForX2Columns(){
        ArrayList<ArrayList<String>> cont = new ArrayList<ArrayList<String>>();
        ArrayList<String> col = new ArrayList<String>();
        ArrayList<Integer> X2Col = new ArrayList<Integer>();
        ArrayList<Integer> X1Col = new ArrayList<Integer>();

        int cols = 0;
        for(int x = 0; x<tableMap.size();x++){
            boolean startOfTable = false;

            ArrayList<String> currentPixel = tableMap.get(x);

            //System.out.println("Im reading a table : " + startOfTable + " Im also have data : " + !col.isEmpty() + " My current Pixel is : " + x + " contains: " +tableMap.get(x));

            if(!currentPixel.isEmpty()&&!startOfTable&&col.isEmpty()){
                startOfTable = true;
                X1Col.add(x);

                System.out.println("Begin of Column: "+X1Col);
                //System.out.println(tableMap.get(x));
                //col = new ArrayList<String>();
                //col.add(tableMap.get(x));
            }
            if(!currentPixel.isEmpty()&&startOfTable){
                col.add(tableMap.get(x).toString());
                //System.out.println(col);
                startOfTable = false;
            }
            if(currentPixel.isEmpty()&&!col.isEmpty()){
                cont.add(col);
                col = new ArrayList<String>();
                X2Col.add(x);
                System.out.println("End of Column: " + X2Col);
                startOfTable = false;
            }
        }
        //System.out.println(cont.get(0));
        //System.out.println(cont.get(1));
        //System.out.println(cont.get(2));
        //System.out.println(X1Col);
        //System.out.println(X2Col);
        this.X2ColumnBoundaries = X2Col;
        this.X1ColumnBoundaries = X1Col;
        return X2Col;
    }
    /*
     * After checking the maps you want to refind the columns
     * This is required for creating the column objects.
     */
    public ArrayList<ArrayList<String>> reFindColumns() throws IOException {
        String[] positions = null;
        int distance =0;
        ArrayList<ArrayList<String>> locationsOfHeaders = new ArrayList<ArrayList<String>>();

        //System.out.println(X1ColumnBoundaries.size());
        //System.out.println(X2ColumnBoundaries.size());

        int counter = 0;
        for(int x : X1ColumnBoundaries){
            for(Element span : spans){
                String word = span.text();
                String pos = span.attr("title");
                positions = pos.split("\\s+");

                if(Integer.parseInt(positions[1]) >= x-1 &&Integer.parseInt(positions[3]) <= X2ColumnBoundaries.get(counter)-1 ){

                    //System.out.println(span.text());

                X1ofCurrentWord = positions[1];

                word = findMergedHeaders(Integer.parseInt(span.attr("ID").replaceAll("\\D", "")), spans, word);
                for(String header:purificationHeaders){
                    if(word.contains(header)){
                        ArrayList<String> locationsOfHeader = new ArrayList<String>();
                        locationsOfHeader.add(header);

                        if(Double.parseDouble(positions[2]) >startOfData){
                            //System.out.println("FOUND"+word);
                            locationsOfHeader.add(X1ofCurrentWord);
                            locationsOfHeader.add(positions[3]);
                            locationsOfHeader.add(positions[2]);
                            locationsOfHeader.add(positions[4]);
                            locationsOfHeaders.add(locationsOfHeader);
                            break;
                        }
                    }
                    else {
                        distance = LD.computeLevenshteinDistance(word, header);
                        if(distance < LDDistance){
                            //System.out.println("FOUND"+word);
                            ArrayList<String> locationsOfHeader = new ArrayList<String>();
                            locationsOfHeader.add(header);

                            if(Double.parseDouble(positions[2]) > startOfData){
                                locationsOfHeader.add(X1ofCurrentWord);
                                locationsOfHeader.add(positions[3]);
                                locationsOfHeader.add(positions[2]);
                                locationsOfHeader.add(positions[4]);
                                locationsOfHeaders.add(locationsOfHeader);
                            }
                        }
                    }
            }   }

        }
        counter++;
        }
        //Now we have a list of the location of Headers. The next step is to use the information to create objects.
        this.locationsOfHeaders = locationsOfHeaders;
        return locationsOfHeaders;
    }

    /*
    * This method creates the columns and puts them in the ArrayList.
    * NOTE that this method only works if the columns have already been created and run trough the refinement methods.
    */
    public void printNewColumns(){
        String[] positions;
        for(int x= 0;x<X1ColumnBoundaries.size();x++){
            System.out.println(X1ColumnBoundaries.get(x) +" " + X2ColumnBoundaries.get(x));
            for(Element span : spans){
                String pos = span.attr("title");
                positions = pos.split("\\s+");
                //System.out.println(lowestY + " " + positions[2]);
                if(Integer.parseInt(positions[1]) >= X1ColumnBoundaries.get(x) && Integer.parseInt(positions[3]) <=X2ColumnBoundaries.get(x)&&Integer.parseInt(positions[2])>=startOfData&&Integer.parseInt(positions[4])<=endOfTable){
                    System.out.println("I got: " + span.text());
                }
            }
            System.out.println("NEW COLUMN!!!");
        }
    }
}