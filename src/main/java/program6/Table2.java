package program6;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//This is a new draft of the Table class. We want to see if we can read/validate/reconstruct on a per line basis.
public class Table2 {

    private int maxY1;
    private Elements spans;
    private String name;
    private ArrayList<Line> table;

    private ArrayList<Line> titleAndHeaders;
    private ArrayList<Line> data;
    private ArrayList<Line> headers;
    private Map<Integer, ArrayList<ArrayList<Element>>> dataByColumn;
    private ArrayList<Line> linesWithMissingData;
    private ArrayList<Column2> dataInColumns;
    private ArrayList<Line> rowSpanners;

    public Table2(Elements spans, double charLengthThreshold){
        this.maxY1 = 0;
        this.spans = spans;
        this.name = "";
        setMaxY1();
        this.table = new ArrayList<Line>();
        createLines(charLengthThreshold);
        //TODO: Find column/rowspans that create lines with just 1 word before filtering
        separateDataByCluster();
        filterLinesThatAreAboveY1();
        //filterDataByType();

        if(data.size()>0){
            System.out.println("~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-");
            System.out.println("RAWData in this table is: ");
            for(Line line : data){
                System.out.println(line.getLine());
            }
        }
        findMissingData();
        findColumns();
        createColumns(charLengthThreshold);
        addLinesWithMissingDataToColumns();

        //TODO: Find out why the title sometimes raises an error: might have something to do with the font of the articles or the OCR.
        if(data.size() > 1){
            System.out.println("In Table: " + getName());
            System.out.println("~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-");
            System.out.println("Data in this table is: ");
            for(Column2 column : dataInColumns){
                System.out.println(column.getColumn());
            }
            if(linesWithMissingData.size() > 0) {
                System.out.println("The following lines were detected for having missing data: ");
                for(Line line : linesWithMissingData){
                    System.out.println(line.getLine());
                }
            }
            System.out.println("~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-");
            if(rowSpanners.size() > 0){
                System.out.println("Correct me if im wrong but the following lines are rowspanners: ");
                for(Line line : rowSpanners){
                    System.out.println(line.getLine());
                }
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
        Line breakingLine = null;
        ArrayList<Line> rowSpanners = new ArrayList<Line>();
        for(Line line : table){
            ArrayList<ArrayList<Element>> clusters = line.getClusters();
            int size = clusters.size();                                          //TODO: IS THIS VARIABLE STILL RELEVANT? TEST!
            if(size <1 && foundData && breakingLine != null){
                break;               //then we have reached the end of the table.
            }
            if(size <1 && foundData && breakingLine==null){
                breakingLine = line;
            }
            else if(size < 2){
                titleAndHeaders.add(line);
                continue;           //we say continue here as it is unlikely that this is data.
            }
            else if(breakingLine == null){
                data.add(line);     //Hooray, data!
                foundData = true;
            }
            else{
                rowSpanners.add(breakingLine);
                breakingLine = null;
            }

        }
        this.titleAndHeaders = titleAndHeaders;
        this.data = data;
        this.rowSpanners = rowSpanners;
    }

    //IMPORTANT: we take the first word of the line, not the entire line. This is because of a failure in the line reading.
    //We assume that line reading is validated elsewhere if any validation is there.
    private void filterLinesThatAreAboveY1(){
        ArrayList<Line> removedLines = new ArrayList<Line>();
        for (Line line : data){
            if(maxY1 > line.getY1OfFirstWord()||maxY1 > line.getY1OfLastWord()){
                System.out.println("Something is wrong, the data is above the title!");
                System.out.println(maxY1 + " " + line.getY1OfFirstWord());
                removedLines.add(line);
            }
        }
        for(Line line : removedLines){
            data.remove(line);
        }
    }

    //This method removes the lines that have missing data and stores them in a separate variable.
    //These lines might contain valuable information about the content or could be a mistake by the OCR or separator.
    //They need special processing in order to be useful.
    private void findMissingData(){
        ArrayList<Line> dataWithoutMissingLines = new ArrayList<Line>();
        ArrayList<Line> linesWithMissingData = new ArrayList<Line>();
        int highestAmountOfClusters = 0;
        for(Line line : data){
            if(line.getClusterSize() > highestAmountOfClusters){
                highestAmountOfClusters = line.getClusterSize();
            }
        }
        for(Line line : data){
            if(line.getClusterSize() < highestAmountOfClusters){
                //Now we now this line got missing data
                linesWithMissingData.add(line);
            }
            else{
                dataWithoutMissingLines.add(line);
            }
        }
        this.linesWithMissingData = linesWithMissingData;
        this.data = dataWithoutMissingLines;
    }

    //TODO: This method works, but when there are more columns then cells in a line it shifts everything to the right.
    private void findColumns(){
        int counterForColumns = 0;
        Map<Integer, ArrayList<ArrayList<Element>>> columnMap = new HashMap<Integer, ArrayList<ArrayList<Element>>>();

        for(Line line : data){
            for(ArrayList<Element>cluster : line.getClusters()){
                if(columnMap.containsKey(counterForColumns)){
                    ArrayList<ArrayList<Element>> fullClusters = columnMap.get(counterForColumns);
                    fullClusters.add(cluster);
                    columnMap.put(counterForColumns, fullClusters);
                    //System.out.println(counterForColumns +" "+ cluster);
                    //System.out.println(columnMap.get(counterForColumns));
                }
                else{
                    ArrayList<ArrayList<Element>> fullClusters = new ArrayList<ArrayList<Element>>();
                    fullClusters.add(cluster);
                    columnMap.put(counterForColumns, fullClusters);
                }
                counterForColumns++;
            }
            line.getClusters();
            counterForColumns = 0;
        }
        this.dataByColumn = columnMap;
    }

    private void createColumns(double AVGCharDistance){
        ArrayList<Column2> dataInColumns = new ArrayList<Column2>();
        System.out.println(dataByColumn.get(2));
        for(int key : dataByColumn.keySet()){
            //System.out.println(dataByColumn.get(key));
            Column2 column = new Column2(dataByColumn.get(key), AVGCharDistance);
            dataInColumns.add(column); 
        }
        this.dataInColumns = dataInColumns;
    }

    private void addLinesWithMissingDataToColumns(){
        ArrayList<Column2> newDataInColumns = new ArrayList<Column2>();
        for(Line line: linesWithMissingData){
            ArrayList<ArrayList<Element>> clusters = line.getClusters();
            for(ArrayList<Element> cluster : clusters){
                for(Column2 column : dataInColumns){
                    if(column.fitsInColumn(Line.getClusterX1(cluster), Line.getClusterX2(cluster)) ||
                            column.columnFitsIn(Line.getClusterX1(cluster), Line.getClusterX2(cluster))){
                        //then we need to add this cluster to that column:
                        newDataInColumns.remove(column);
                        column.addCell(cluster);
                        newDataInColumns.add(column);
                    }
                }
            }
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
