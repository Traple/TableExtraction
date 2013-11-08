package program6;


import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/*
 * The table class contains methods and properties of the table.
 * You can call getters and setters to access the various properties of a Table.
 * It also contains methods calling it's subclass Column to change it's attributes.
 */
public class Table {
    private Elements spans;
    public static Logger LOGGER = Logger.getLogger(Table.class.getName());
    private Map<Integer, ArrayList<String>> tableMap;
    private ArrayList<Integer> X2ColumnBoundaries = new ArrayList<Integer>();
    private ArrayList<Integer> X1ColumnBoundaries = new ArrayList<Integer>();
    private int endOfTable;
    private int beginOfTable;
    private ArrayList<ArrayList<Element>> columns;
    private String name;
    private double distanceConstant;
    private double distanceThreshold;

    /**
     * The constructor of this class consists of two parts: the first part sets some of the local variables that are required
     * for the initial reconstruction of the Table. The second part calls methods
     * @param spans the spans that are below the span table, which was picked up in the pages class.
     * @param workLocation The worklocation as specified by the user.
     * @param file The file which was used to extract the table from.
     * @throws IOException
     */
    public Table(Elements spans, String workLocation, File file, double spaceDistance) throws IOException {
        //first part of the constructor:
        System.out.println("Table Created.");
        try{
            String name = spans.get(0).text() + " " + spans.get(2).text() + " " + spans.get(3).text() + " " + spans.get(4).text() + " " + spans.get(5).text();
        }
        catch(IndexOutOfBoundsException e){
            System.out.println("To little spans!");
            String name = "I only have " +spans.size() + " words.";
        }
        LOGGER.info("New Table. The title roughly starts with: "+ name);
        LOGGER.info("I was found in: " + file.getName());
        System.out.println("My name is: " + name);

        this.spans = spans;
        this.columns = new ArrayList<ArrayList<Element>>();
        this.name = name;
        //~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-
        //Second part of the constructor:
        System.out.println("Table reconstruction time:");

        Scores score = new Scores(spans, spaceDistance);
        this.endOfTable = score.findEndOfTable();
        this.beginOfTable = score.findBeginOfTable();
        this.distanceConstant = score.getDistanceConstant();

        LOGGER.info("Begin of the table at " + beginOfTable);
        LOGGER.info("End of the table at: " + endOfTable);

        System.out.println("Begin of the table at " + beginOfTable);
        System.out.println("End of the table at: " + endOfTable);
        this.tableMap = createTableMap(endOfTable);
        checkMapForX2Columns();

        setColumnsContent();

        String content = "<results>\n"; //used for the XML output
        String data = "";

        for (ArrayList<Element> column : columns){
            try{
            Column col = new Column(column);
            data = data + col.getColumnContentInXML();
            this.distanceThreshold = col.getHeightThreshold();
            }
            catch(NullPointerException e){
                continue;
            }
        }
            content = content + data;
            content = content + "</results>\n";
            System.out.println("Writing to file: ");
            write(content, workLocation+"/results/" +name+".xml", file);
    }

    /**
     * This method creates a map that links the words in the rowspans to the pixels in the table. This is done horizontally
     * so it matches every pixel on the X-as with all the words the collide with that pixel
     * @param endOfTable The found end of the table.
     * @return A map containing integer:ArrayList<String> in other words: pixel:List of words that collide with this pixel.
     */
    private Map<Integer, ArrayList<String>> createTableMap(int endOfTable) {
        Map<Integer, ArrayList<String>> tableMap = new HashMap<Integer, ArrayList<String>>();
        int counter = 0;
        String[] positions;
        while (counter < 10000) {
            ArrayList<String> pixelContent = new ArrayList<String>();
            for (Element span : spans) {
                String pos = span.attr("title");
                positions = pos.split("\\s+");
                int x1 = Integer.parseInt(positions[1]);
                int x2 = Integer.parseInt(positions[3]);
                int y1 = Integer.parseInt(positions[2]);
                int y2 = Integer.parseInt(positions[4]);
                if (counter >= x1 && counter <= x2 && y1 >= beginOfTable && y2 < endOfTable) {
                    pixelContent.add(span.text());
                }
            }
            tableMap.put(counter, pixelContent);
            counter++;
        }
        return tableMap;
    }

    //TODO: This method should find the column boundaries on a per line basis. Overall searching does not give the expected results
    //this method tries to find the X2 of each column using local variables.
    private void checkMapForX2Columns() {
        ArrayList<String> col = new ArrayList<String>();
        ArrayList<Integer> X2Col = new ArrayList<Integer>();
        ArrayList<Integer> X1Col = new ArrayList<Integer>();



        for (int x = 0; x < tableMap.size(); x++) {

            boolean startOfTable = false;

            ArrayList<String> currentPixel = tableMap.get(x);

            if (!currentPixel.isEmpty() && !startOfTable && col.isEmpty()) {
                startOfTable = true;
                X1Col.add(x);

            }

            if (!currentPixel.isEmpty() && startOfTable) {
                col.add(tableMap.get(x).toString());
                startOfTable = false;
            }
            if (currentPixel.isEmpty() && !col.isEmpty()) {
                col = new ArrayList<String>();
                X2Col.add(x);
                startOfTable = false;
            }
        }
        System.out.println("Begin of Column: " + X1Col);
        LOGGER.info("Begin of the Column: " + X1Col);
        System.out.println("End of Column: " + X2Col);
        LOGGER.info("End of Column: " + X2Col);
        this.X2ColumnBoundaries = X2Col;
        this.X1ColumnBoundaries = X1Col;
    }

    /**
     * This methods searches for the content of the column using local variables.
     */
    private void setColumnsContent(){
        ArrayList<ArrayList<Element>> columns = new ArrayList<ArrayList<Element>>();
        String[] positions;
        for (int x = 0; x < X1ColumnBoundaries.size(); x++) {
            ArrayList<Element> column = new ArrayList<Element>();
            for (Element span : spans) {
                String pos = span.attr("title");
                positions = pos.split("\\s+");
                if (Integer.parseInt(positions[1]) >= X1ColumnBoundaries.get(x) && Integer.parseInt(positions[3]) <= X2ColumnBoundaries.get(x)&& Integer.parseInt(positions[4]) <= endOfTable && Integer.parseInt(positions[4])>=beginOfTable){
                    column.add(span);
                }
            }
            if(column.size() > 3){
                columns.add(column);
            }
        }
        this.columns = columns;
    }

    //TODO: Chose a better way to store the tables (create a different name), to avoid overwriting.
    /**
     * This method writes the results to the results directory in the workspace. Output is in XML.
     * @param filecontent The results as being collected during the reconstruction of this table.
     * @param location The path to the the XML file (output).
     * @param file The file which was used to reconstruct this table. (used for provenance purpose)
     * @throws IOException
     */
    public void write(String filecontent, String location, File file) throws IOException{
        FileWriter fileWriter;
        filecontent = getProvenance(file)+ filecontent;

        File newTextFile = new File(location);
        fileWriter = new FileWriter(newTextFile);
        fileWriter.write(filecontent);
        fileWriter.close();
    }

    /**
     * This method creates the provenance that is being used for writing the output.
     * @param file The file which was used to create this table.
     * @return A string containing the provenance in XML format.
     */
    public String getProvenance(File file){
        return "<provenane>\n"+
                "<fromFile>" + file.getName()+"</fromFile>\n" +
                "<nameOfTable>" + name + "</nameOfTable>\n"
                +"<columnDistanceConstant>"+distanceConstant+"</columnDistanceConstant>\n"+
                "<rowDistanceConstant>"+distanceThreshold+"</rowDistanceConstant>\n"+
                "</provenance>\n";
    }

    //TODO: Create a validation method that merges all the values and score them.
}