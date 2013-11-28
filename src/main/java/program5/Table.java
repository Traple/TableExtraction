package program5;


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
 * You can call getters and setters to acces the various properties of a Table2.
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
    private String workLocation;
    private ArrayList<ArrayList<Element>> columns;
    private String name;
    private double distanceConstant;
    private double distanceThreshold;

    public Table(Elements spans, String workLocation, File file) throws IOException {
        System.out.println("Table2 Created.");
        String name = spans.get(0).text() + " " + spans.get(2).text() + " " + spans.get(3).text() + " " + spans.get(4).text() + " " + spans.get(5).text();
        LOGGER.info("New Table2. The title roughly starts with: "+ name);
        LOGGER.info("I was found in: " + file.getName());
        System.out.println("My name is: " + name);

        this.workLocation = workLocation;
        this.spans = spans;
        this.columns = new ArrayList<ArrayList<Element>>();
        this.name = name;
        //~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-
        System.out.println("Now we recreate the table using the positions. This might add more data!");

        Scores score = new Scores(spans);
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

        //TODO: Refine to column one more time so it also give the full header.

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

    //Dont use this method just jet. We need to fix the columnscoring first.
    //TODO: What this method needs is the end of the table!!! For this it also needs to know the end of the row!!!
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

    /*
    * This method creates the columns and puts them in the ArrayList.
    * NOTE that this method only works if the columns have already been created and run trough the refinement methods.
    */
    public void printNewColumns() {
        for(ArrayList<Element> column : columns){
            for(Element span : column){
                System.out.print(span.text() + ", ");
            }
            System.out.println();
        }
    }

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

    public void write(String filecontent, String location, File file) throws IOException{
        FileWriter fileWriter;
        filecontent = getProvenance(file)+ filecontent;

        File newTextFile = new File(location);
        fileWriter = new FileWriter(newTextFile);
        fileWriter.write(filecontent);
        fileWriter.close();
    }

    public String getProvenance(File file){
        String provenance = "<provenane>\n"+
                "<fromFile>" + file.getName()+"</fromFile>\n" +
                "<nameOfTable>" + name + "</nameOfTable>\n"
                +"<columnDistanceConstant>"+distanceConstant+"</columnDistanceConstant>\n"+
                "<rowDistanceConstant>"+distanceThreshold+"</rowDistanceConstant>\n"+
                "</provenance>\n";
        return provenance;
    }
}