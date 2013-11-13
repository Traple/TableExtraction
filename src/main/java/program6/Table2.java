package program6;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//This is a new draft of the Table class. We want to see if we can read/validate/reconstruct on a per line basis.
//TODO: Finish this draft and make sure it works and improves TEA
public class Table2 {

    private int maxY1;
    private Elements spans;
    private String name;
    private ArrayList<Line> table;

    private ArrayList<Line> titleAndHeaders;
    private ArrayList<Line> data;
    private ArrayList<Line> headers;

    public Table2(Elements spans, double charLengthThreshold){
        this.maxY1 = 0;
        this.spans = spans;
        this.name = "";
        setMaxY1();
        this.table = new ArrayList<Line>();
        createLines(charLengthThreshold);
        separateDataByCluster();
        filterLinesThatAreAboveY1();
        //filterDataByType();

        setColumns();

        if(data.size() > 1){
            System.out.println("In Table: " + getName());
            System.out.println("~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-");
            System.out.println("Data in this table is: ");
            for(Line line : data){
                System.out.println(line.getLine());
            }
            System.out.println("~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-");
            if(headers != null){
                System.out.println("I dont get headers yet. But it must be somewhere in here: ");
                for(Line line : headers){
                    System.out.println(line.getLine());
                }
            }
            else{
                System.out.println("There were no headers or the headers alligned nicely with the data.");
            }
        }
    }

    //assumption:
    //In order for words to be on the same row:
    //X1 of word should be > X2 of last word.
    private void setMaxY1(){
        try{
        String[] positions;
        String pos = spans.get(0).attr("title");
        positions = pos.split("\\s+");
        int lastX2 = 0;

        for(Element span : spans){
            pos = span.attr("title");
            positions = pos.split("\\s+");
            int x1 = Integer.parseInt(positions[1]);
            int x2 = Integer.parseInt(positions[3]);
            int y1 = Integer.parseInt(positions[2]);

            if(!(x1>=lastX2)){
                break;
            }
            name = name + span.text() + " ";
            if(y1>maxY1){
                this.maxY1 = y1;
            }
            lastX2 = x2;
            }
        }
        catch (IndexOutOfBoundsException e){
            System.out.println("This table got a weird name it raised the following error: ");
            System.out.println(e);
        }
    }

    //TODO: Method crashes when encountering the end of the page.
    public void createLines(double charLengthThreshold){
        String pos;
        String[] positions;
        int lastX2 = 0;
        Elements currentLine = new Elements();

        for(Element span : spans){
            pos = span.attr("title");
            positions = pos.split("\\s+");
            int x1 = Integer.parseInt(positions[1]);
            int x2 = Integer.parseInt(positions[3]);
            int y1 = Integer.parseInt(positions[2]);
            int y2 = Integer.parseInt(positions[4]);

            if(!(x1>=lastX2)){
                Line line = new Line(currentLine, charLengthThreshold);
                table.add(line);
                currentLine = new Elements();
            }
            lastX2 = x2;
            currentLine.add(span);
        }
    }

    /**
     * Does the types of data match?
     * Does the positions of the data overlap?
     * Can we find a cutoff point, define and maintain the pattern.
     *
     * Columns need: Data.
     */
    private void separateDataByCluster(){
        ArrayList<Line> titleAndHeaders = new ArrayList<Line>();
        ArrayList<Line> data = new ArrayList<Line>();
        boolean foundData = false;
        for(Line line : table){
            ArrayList<ArrayList<Element>> clusters = line.getClusters();
            int size = clusters.size()+1;
            if(size <2 && foundData){
                break;               //then we have reached the end of the table.
            }
            else if(size < 2){
                titleAndHeaders.add(line);
                continue;           //we say continue here as it is unlikely that this is data.
            }
            else{
                data.add(line);     //Hooray, data!
                foundData = true;
            }

        }
        this.titleAndHeaders = titleAndHeaders;
        this.data = data;
    }

    //IMPORTANT: we take the first word of the line, not the entire line. This is because of a failure in the line reading.
    //We assume that line reading is validated elsewhere if any validation is there.
    //IMPORTANT: So far we didn't have any detections with this method. Consider scrapping.
    private void filterLinesThatAreAboveY1(){
        for (Line line : data){
            if(maxY1 > line.getY1OfFirstWord()){
                System.out.println("Something is wrong, the data is above the title!");
                System.out.println(maxY1 + " " + line.getY1OfFirstWord());
            }
        }
    }

    private void setColumns(){
        int counterForColumns = 0;
        Map<Integer, ArrayList<Element>> columnMap = new HashMap<Integer, ArrayList<Element>>();

        for(Line line : data){
            for(ArrayList<Element>cluster : line.getClusters()){
                if(columnMap.containsKey(counterForColumns)){
                    ArrayList<Element> fullClust = columnMap.get(counterForColumns);
                    fullClust.addAll(cluster.subList(0,cluster.size()));
                    columnMap.put(counterForColumns, fullClust);
                }
                else{
                    columnMap.put(counterForColumns, cluster);
                }
                System.out.println(columnMap);
                counterForColumns++;
            }
            line.getClusters();
            counterForColumns = 0;
        }
    }

    //Now we need to check the types, found in the data. This works as an extra filter.
    private void filterDataByType(){
        ArrayList<String> lastLine = new ArrayList<String>();
        ArrayList<String> currentLine;
        ArrayList<Line> filteredData = new ArrayList<Line>();
        ArrayList<Line> possibleHeaders = new ArrayList<Line>();
        Line lastLineObject = null;
        boolean foundData = false;                    //if we havent found any data, but there is a cluster, that might be the headers.
        boolean firstDetection = true;        //We need this boolean, because if we detect a pattern for the first time we want to add the first line as well.
        for(Line line : data){
            currentLine = line.getClusterTypes();
            System.out.println(currentLine);
            System.out.println(line.getLine());
            System.out.println(line.getClusters());
            if(currentLine.equals(lastLine)){
                if(firstDetection){
                    filteredData.add(lastLineObject);
                }
                filteredData.add(line);
                firstDetection = false;
                foundData = true;
            }
            else if(!foundData){
                possibleHeaders.add(line);
            }
            lastLineObject = line;
            lastLine = currentLine;
        }
        this.data = filteredData;
        try{
            possibleHeaders.remove(possibleHeaders.size()-1);          //you need to remove the last one cause size is not the index.
            this.headers = possibleHeaders;
        }
        catch(IndexOutOfBoundsException e){
            //then there was an empty line. We filter those out as well :)
        }
    }

    public String getName(){
        return name;
    }
}
