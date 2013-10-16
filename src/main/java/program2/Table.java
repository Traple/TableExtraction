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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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

    private Set<String> purificationHeaders = new HashSet<String>();
    private Collection<Purification> Pheaders;
    private ArrayList<ArrayList<String>> locationsOfHeaders;

    public Table(int LDDistance, Elements spans){
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

    //TODO: Refine the findMergedHeaders method so it will catch any double word headers succesfuly.
    //TODO: Add the LevenshteinDistance class in this method so it can use the distance in the prefix. Watch out for false positives.
    //TODO: Make sure that the mergeHeaders also increases the positions and not just the names!!

    public String findMergedHeaders(int wordID, Elements spans, String header) throws IOException {
        String mergedHeader =header; //the merged header equals the current header. This is only changed if there is a prefix found.

        String currentID;
        Element previousSpan = null;
        String[] positions;
        for(Element span : spans){
            currentID = span.attr("ID").replaceAll("\\D", "");
            if(Integer.parseInt(currentID) == wordID){
                try{
                    //System.out.println("Trying to merge: "+ span.text() + " with "+ previousSpan.text());
                    if(previousSpan.text().equals("Total")||previousSpan.text().equals("Specific")||previousSpan.text().equals("Purification")||previousSpan.text().equals("Puriﬁcation")||previousSpan.text().equals("Speciﬁc")||previousSpan.text().equals("Specific")){
                        //System.out.println("NEED TO MERGE: " + span.text() + " with "+ previousSpan.text());
                        mergedHeader = previousSpan.text() + " "  + span.text();
                        String pos = previousSpan.attr("title");
                        positions = pos.split("\\s+");
                        X1ofCurrentWord = positions[1];
                        //System.out.println(mergedHeader);
                    }}
                catch(NullPointerException e){

                }
            }
            previousSpan = span;
        }
        return mergedHeader;
    }
    public ArrayList<Column> createColumns(){
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
            Column col = new Column(header, X1, X2, Y1, Y2, spans, Pheaders);
            col.fillCells(col.columnChecker());
            System.out.println(col.findHeaderTypes());

            columns.add(col);
        }
        return columns;
    }


}