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

/**
 * this class is called Table2 because it rewritten from scratch using only some of the methods used in the Table2 class
 * of TEA 0.5. The developer apologizes for any inconvenience.
 */
public class Table2 {

    public static Logger LOGGER = Logger.getLogger(Table2.class.getName());
    private int maxY1;
    private Elements spans;
    private String name;
    private ArrayList<Line> table;
    private double averageLineDistance;

    private ArrayList<Line> titleAndHeaders;
    private ArrayList<Line> data;
    private Map<Integer, ArrayList<ArrayList<Element>>> dataByColumn;
    private ArrayList<Line> linesWithMissingData;
    private ArrayList<Column2> dataInColumns;
    private ArrayList<Line> rowSpanners;
    private Validation validation;
    private double lineDistanceModifier;

    public Table2(Elements spans, double charLengthThreshold, File file, String workspace, int tableID) throws IOException {
        this.maxY1 = 0;
        this.spans = spans;
        this.name = "";
        this.lineDistanceModifier = 1.0;          //TODO: Validate this threshold.

        this.validation = new Validation();
        if(spans.size() > 0){
        setMaxY1();
        this.table = new ArrayList<Line>();
        createLines(charLengthThreshold);

        separateDataByCluster();
        filterLinesThatAreAboveY1();
        //filterDataByType();

        if(data.size()>1){
            System.out.println(data.size());
            System.out.println(data);
            System.out.println("~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-");
            System.out.println("RAWData in this table is: ");
            for(Line line : data){
                validation.setClusterCertainty(line.getDistances(), line.getDistanceThreshold());   //TODO: WRONG, we need to check the actual parsed data.
                validation.setLineThreshold(line.getDistanceThreshold());
                System.out.println(line);
            }
            filterEmptyLines();
            findMissingData();
//            System.out.println("Missing Data:");
//            System.out.println(linesWithMissingData);
//            System.out.println("Data: ");
//            System.out.println(data);
            findColumns();
            createColumns(charLengthThreshold);
            addLinesWithMissingDataToColumns();
            checkTheTitle();
        }
        else {
            LOGGER.info("The word Table2 was detected but no clusters were found.\n" +
                    "It was found at position: " + maxY1);
        }

        //TODO: Find out why the title sometimes raises an error: might have something to do with the font of the articles or the OCR.
        if(data.size() > 1){
            for(Line line : data){
                validation.setClusterCertainty(line.getDistances(), line.getDistanceThreshold());
                validation.setLineThreshold(line.getDistanceThreshold());
            }
            LOGGER.info("Table2: " + getName());
            System.out.println("In Table2: " + getName());
            System.out.println("~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-");
            System.out.println("Data in this table is: ");
            for(Column2 column : dataInColumns){
                System.out.println(column);
            }
            if(linesWithMissingData.size() > 0) {
                System.out.println("~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-");
                System.out.println("The following lines were detected for having missing data or it was a line that had more clusters then the rest of the table.: ");
                for(Line line : linesWithMissingData){
                    System.out.println(line);
                }
            }
            System.out.println("~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-");
            if(rowSpanners.size() > 0){
                System.out.println("Correct me if im wrong but the following lines are rowspanners: ");
                for(Line line : rowSpanners){
                    System.out.println(line);
                }
            }
            System.out.println("Validation:\n" + validation);
//            write(getXMLContent(file, tableID), (workspace+"/results") ,file, tableID);
        }
        }
    }

    private void filterEmptyLines() {
        ArrayList<Line> newTable = new ArrayList<Line>();
        for(Line line : table){
            if(!(line.getClusterSize() < 1)){
                newTable.add(line);
            }
        }
        this.table = newTable;
    }

    //assumption:
    //In order for words to be on the same row:
    //X1 of word should be > X2 of last word.
    private void setMaxY1(){
        Element lastSpan = null;

        String[] positions;
        String pos;
        int lastX2 = 0;
        for(Element span : spans){
            try{
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
            lastSpan = span;
        }


        catch (IndexOutOfBoundsException e){
            System.out.println("This table got a weird name it raised the following error: ");
            System.out.println(lastSpan.text());
            System.out.println(e);
        }
    }
    }

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

            if(!(x1>=lastX2)){
                Line line = new Line(currentLine, charLengthThreshold);
                table.add(line);
                currentLine = new Elements();
            }
            lastX2 = x2;
            currentLine.add(span);
        }
        if(currentLine.size() > 4){                 //For in case the last line is part of the table
            Line line = new Line(currentLine, charLengthThreshold);                 //TODO: CHECK FOR FALSE POSITIVES!
            table.add(line);
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
            int size = clusters.size();
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
                //System.out.println(line);
                data.add(line);     //Hooray, data!
                foundData = true;
            }
            else{
                rowSpanners.add(breakingLine);
                data.add(line);
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
    private void findAverageLineHeightDistance(){
        double y1;
        double lastY2 = -999;
        double totalDistance = 0.0;
        double averageDistance;

        for(Line line : data){
            if(lastY2 == -999){
                lastY2 = line.getAverageY2();
                continue;
            }
            y1 = line.getAverageY1();
            totalDistance = totalDistance + (y1 - lastY2);
            lastY2 = line.getAverageY2();
        }
        averageDistance = totalDistance/data.size();
        this.averageLineDistance = averageDistance;
        this.validation.setAverageDistanceBetweenRows(averageDistance);
    }

    //This method removes the lines that have missing data and stores them in a separate variable.
    //These lines might contain valuable information about the content or could be a mistake by the OCR or separator.
    //They need special processing in order to be useful.

    private void findMissingData(){
        ArrayList<Line> dataWithoutMissingLines = new ArrayList<Line>();
        ArrayList<Line> linesWithMissingData = new ArrayList<Line>();
        ArrayList<Integer> numberOfClusters = new ArrayList<Integer>();

        for(Line line : data){
            numberOfClusters.add(line.getClusterSize());
        }
        if(numberOfClusters.size()>0){
            int mostFrequentAmountOfClusters = CommonMethods.mostCommonElement(numberOfClusters);
            validation.setMostFrequentNumberOfClusters(mostFrequentAmountOfClusters);
            for(Line line : data){
                if(line.getClusterSize() < mostFrequentAmountOfClusters){
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
    }

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
        //System.out.println(dataByColumn.get(2));
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
//            System.out.println("miss the data: " + line);
            ArrayList<ArrayList<Element>> clusters = line.getClusters();
            for(ArrayList<Element> cluster : clusters){
                for(Column2 column : dataInColumns){
                    if(column.fitsInColumn(Line.getClusterX1(cluster), Line.getClusterX2(cluster)) ||
                            column.columnFitsIn(Line.getClusterX1(cluster), Line.getClusterX2(cluster))){
                        //then we need to add this cluster to that column:

//                        System.out.println("Column is: " + column);
//                        System.out.println(column.getColumnBoundaryX1() +" "+ column.getColumnBoundaryX2());
//                        System.out.println("I want to add: " + cluster);
//                        System.out.println("I fit in the column: " + column.fitsInColumn(Line.getClusterX1(cluster), Line.getClusterX2(cluster)) + " " +
//                                "\nColumn fits in me: " +column.columnFitsIn(Line.getClusterX1(cluster), Line.getClusterX2(cluster)));
                        newDataInColumns.remove(column);
                        column.addCell(cluster);
                        newDataInColumns.add(column);

                    }

                    //TODO: NEEDS VALIDATION SCORE. USE THE 1,2,3 System!
                    else if(column.touchesColumn(Line.getClusterX1(cluster), Line.getClusterX2(cluster))){
                        System.out.println("CLUSTER: " + cluster + " touches: " + column);
                        newDataInColumns.remove(column);
                        column.addCell(cluster);
                        newDataInColumns.add(column);
                    }
                }
            }
        }
    }
    //TODO: Set up validation for this method (WHY is this a rowspanner?)
    //This method checks if the last sentence of the title should be in the headers.
    private void checkTheTitle(){
        findAverageLineHeightDistance();
        if(!(titleAndHeaders.size() < 3)){
        Line lastCellInTitle = titleAndHeaders.get(titleAndHeaders.size()-1);

        double distanceBetweenTitle = lastCellInTitle.getAverageY1()- titleAndHeaders.get(titleAndHeaders.size()-2).getAverageY2();
        int minY1Column = Integer.MIN_VALUE;
        for(Column2 column : dataInColumns){
            if(column.getMinY1() > minY1Column){
                minY1Column = column.getMinY1();
            }
        }

        if((averageLineDistance * lineDistanceModifier) < distanceBetweenTitle){
            rowSpanners.add(titleAndHeaders.get(titleAndHeaders.size()-1));
            titleAndHeaders.remove(titleAndHeaders.size()-1);
            this.validation.calculateTitleConfidence(averageLineDistance, distanceBetweenTitle,lineDistanceModifier);
//            checkTheTitle();
            }
        }
    }

    //Now we need to check the types, found in the data. This works as an extra filter.
    //TODO: Use this method as a starting point for the OCR correction
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
        }
        catch(IndexOutOfBoundsException e){
            //then there was an empty line. We filter those out as well :)
        }
    }
    private String getXMLContent(File file, int tableID){
        String fileContent = "<TEAFile>\n"+ getProvenance(file , tableID);
        fileContent = fileContent + "    <results>\n";
        fileContent = fileContent + "        <title>" + CommonMethods.changeIllegalXMLCharacters(name) + "</title>\n" ;
        fileContent = fileContent + "        <title>" + CommonMethods.changeIllegalXMLCharacters(titleAndHeaders.toString()) + "</title>\n" ;
        fileContent = fileContent + "        <columns>\n";
        for(Column2 column : dataInColumns){
            fileContent = fileContent + "            <column>"+ CommonMethods.changeIllegalXMLCharacters(column.toString())+"</column>\n";
        }
        fileContent = fileContent + "        </columns>\n";
        if(rowSpanners.size() > 0){
            fileContent = fileContent +"        <rowSpanners>\n";
            for(Line line : rowSpanners){
                fileContent = fileContent +"            <rowSpanner>" +CommonMethods.changeIllegalXMLCharacters(line.toString()) + "</rowSpanner>\n";
            }
            fileContent = fileContent + "        </rowSpanners>\n";
        }
        fileContent = fileContent + "    </results>\n";
        fileContent = fileContent + validation.toXML();
        fileContent = fileContent + "</TEAFile>";

        return fileContent;
    }

    /**
     * This method writes the results to the results directory in the workspace. Output is in XML.
     * @param filecontent The results as being collected during the reconstruction of this table.
     * @param location The path to the the XML file (output).
     * @param file The file which was used to reconstruct this table. (used for provenance purpose)
     * @throws java.io.IOException
     */
    private void write(String filecontent, String location, File file, int tableID) throws IOException {
        LOGGER.info("Writing to file: " + location + " " + file.getName() + " " + tableID);
        FileWriter fileWriter;
//        filecontent = getProvenance(file)+ filecontent;
        location = location + "\\" + file.getName() + "-" + tableID+ ".xml";
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
    private String getProvenance(File file, int tableID){
        return "    <provenane>\n"+
                "        <fromFile>" + file.getName()+"</fromFile>\n" +
                "        <user>Sander</user>\n"+
                "        <detectionID>" + tableID +"</detectionID>\n"+
                "    </provenance>\n";
    }

    public String getName(){
        return name;
    }
}
