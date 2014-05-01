package program8;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * this class is called Table2 because it rewritten from scratch using only some of the methods used in the Table2 class
 * of TEA 0.5. The developer apologizes for any inconvenience this may cause.
 */
public class Table2 {
    public static Logger LOGGER = Logger.getLogger(Table2.class.getName());
    private final double verticalThresholdModifier;
    private final double horizontalThresholdModifier;
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
    private ArrayList<ArrayList<Element>> missPlacedCells;
    private int minY1;
    private int minX1;
    private int maxX2;
    private int maxY2;

    /**
     * This is the constructor of the table class. It takes it's parameters and sets them as local variables.
     * It also puts the default values for the rest of the table and then starts calling the other methods in this class.
     * to extract the table according to the rules of TEA.
     * @param spans These are the words below the table detection from the Page class.
     * @param charLengthThreshold This is the character length threshold as calculated in the Page class.
     * @param file This is the File that was used to extract the table from. It is only used for the creation of provenance.
     * @param workspace This is the workspace as specified by the user.
     * @param tableID This is the ID of the detected table. It is mainly used for the creation of the output file and for provenance.
     * @param verticalThresholdModifier The modifier from the configuration file that should be used to indicate how much space there should be between lines
     * @param horizontalThresholdModifier The modifier used for creating the threshold in horizontal partitioning.
     * @param averageLineDistance The average (vertical) distance between lines as calculated in the Page class.
     * @param debugging is true if the program is in debugging mode.
     * @param allowedHeaderIterations The amount of iterations that the program is allowed to run, searching for headers.
     * @param allowedHeaderSize The amount of headers supported by the program. Implemented as a last cut-off if thresholding fails.
     * @throws IOException When one of the files cant be found
     */
    public Table2(Elements spans, double charLengthThreshold, File file, String workspace, int tableID, double verticalThresholdModifier,
                  double horizontalThresholdModifier, double averageLineDistance, boolean debugging, int allowedHeaderSize, int allowedHeaderIterations)
            throws IOException {
        String debugContent = "";
        this.averageLineDistance = averageLineDistance;
        this.maxY1 = 0;
        this.spans = spans;
        this.name = "";
        this.horizontalThresholdModifier = horizontalThresholdModifier;
        this.verticalThresholdModifier = verticalThresholdModifier;
        this.validation = new Validation();
        this.validation.setAverageDistanceBetweenRows(averageLineDistance);

        if(spans.size() > 0){
            setMaxY1();
            this.table = new ArrayList<Line>();

            createLines(charLengthThreshold);
            separateDataByCluster();
            filterLinesThatAreAboveY1();

        if(data.size()>1){
            System.out.println(getRawTable());
            debugContent = debugContent + getRawTable()+ "\n";
            filterEmptyLines();
            findMissingData();
            findColumns();
            createColumns(charLengthThreshold);
            checkColumns();
            debugContent = debugContent + "lines with missing data: " + linesWithMissingData + "\n";
            if(linesWithMissingData!=null){
                addLinesWithMissingDataToColumns();
            }
            fillBlankCells();
        }
            else {
                LOGGER.info("The word Table was detected but no clusters were found.\n" +
                    "It was found at position: " + maxY1);
        }
        if(data.size() > 1){
            for(Line line : data){
                validation.setClusterCertainty(line.getDistances(), line.getDistanceThreshold());
                validation.setLineThreshold(line.getDistanceThreshold());
            }
            LOGGER.info("Table: " + getName());
            System.out.println("In Table: " + getName());
            System.out.println("~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-");
            System.out.println("Data in this table is: ");

            ArrayList<Integer> distances = new ArrayList<Integer>();
            Column2 lastColumn =null;
            for(Column2 column : dataInColumns){
                System.out.println(column);
                if(dataInColumns.indexOf(column) == 0){
                    lastColumn = column;
                    continue;
                }
                if (lastColumn != null) {
                    distances.add(column.getAverageX1()-lastColumn.getAverageX2());
                }
            }
            validation.setClusterCertainty(distances,averageLineDistance*horizontalThresholdModifier);
            if(linesWithMissingData != null && linesWithMissingData.size() > 0) {
                System.out.println("~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-");
                System.out.println("The following lines were detected for having missing data or it was a line that had more clusters then the rest of the table.: ");
                for(Line line : linesWithMissingData){
                    System.out.println(line);
                }
            }
           System.out.println("~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-");
            if(rowSpanners.size() > 0){
                System.out.println("Potential rowspanners: ");
                for(Line line : rowSpanners){
                    System.out.println(line);
                }
            }
            System.out.println("Validation:\n" + validation);
            System.out.println(table);
            setClusterCertainties();
            System.out.println("Checking out the semantics.");
            SemanticFramework semanticFramework = new SemanticFramework(dataInColumns, (averageLineDistance * verticalThresholdModifier),
                    rowSpanners, charLengthThreshold * horizontalThresholdModifier, table, validation, titleAndHeaders, allowedHeaderSize, allowedHeaderIterations);
            System.out.println("Checking for false positive...");
            checkForFalsePositive();
            System.out.println("False positive: " + validation.getFalsePositive());
            LOGGER.info("False positive: " + validation.getFalsePositive());
            System.out.println();
            fillBlankCells();
            System.out.println(semanticFramework);
            System.out.println("Calculating final table statistics.");
            setTableBoundaries(semanticFramework);
            System.out.println("Now writing to file.");
            write2((workspace), file, tableID, semanticFramework);         //write: getXMLContent(file, tableID, semanticFramework.getXML()),
            if(debugging){
                writeDebugFile(debugContent, workspace, file);
            }
        }
        else{
            LOGGER.info("All the found data was filtered out!");
        }
        }
    }

    /**
     * This method filters the lines that contains less the 1 cluster but ended up in the table anyway.
     */
    private void filterEmptyLines() {
        ArrayList<Line> newTable = new ArrayList<Line>();
        for(Line line : table){
            if(!(line.getClusterSize() < 1)){
                newTable.add(line);
            }
            else if (!(newTable.isEmpty())){
                newTable.add(line);
            }
        }
        this.table = newTable;
    }

    /**
     * This method sets the Y1 position of the table. This is the highest pixel in the table (with the lowest Y1 score).
     */
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
                if (lastSpan != null) {
                    System.out.println(lastSpan.text());
                }
                System.out.println(e);
            }
        }
    }
    /**
     * This method creates the lines by reading trough the file.
     * @param charLengthThreshold This is the average characterLength as calculated in the Page class.
     */
    public void createLines(double charLengthThreshold){
        String pos;
        String[] positions;
        int lastX2 = 0;
        int lastY2 = 0;
        Elements currentLine = new Elements();

        int lineNumber = 0;

        for(Element span : spans){
            pos = span.attr("title");
            positions = pos.split("\\s+");
            int x1 = Integer.parseInt(positions[1]);
            int y1 = Integer.parseInt(positions[2]);
            int x2 = Integer.parseInt(positions[3]);
            int y2 = Integer.parseInt(positions[4]);

            //This is where the modifier can be placed for the 1,2,3 parameter as described in the version test:
            if(((x1<=lastX2)||y1>lastY2 || CommonMethods.calcDistance(lastY2, y1)>(averageLineDistance*verticalThresholdModifier))
                    &&spans.indexOf(span)!=0){
                Line line = new Line(currentLine, charLengthThreshold, horizontalThresholdModifier);
//                System.out.println(line);
                line.setLineNumber(lineNumber);
                table.add(line);
                currentLine = new Elements();
                lineNumber+=1;
            }
            lastX2 = x2;
            lastY2 = y2;
            currentLine.add(span);
        }
        if(currentLine.size() > 4){                 //For in case the last line is part of the table
            Line line = new Line(currentLine, charLengthThreshold, horizontalThresholdModifier);
            table.add(line);
        }
    }

    /**
     * This method separates the data based on the amount of partitions (clusters) in each line.
     * The lines before we can find any lines with partitions are stored in the private titleAndHeaders variable
     * The lines that contain partitions are stored in the private data variable
     * The lines that were inside the data lines and contained just one partition are stored in the private rowspanner variable.
     */
    private void separateDataByCluster(){
        ArrayList<Line> titleAndHeaders = new ArrayList<Line>();
        ArrayList<Line> data = new ArrayList<Line>();
        boolean foundData = false;
        Line breakingLine = null;
        Line doubleBreakingLine = null;
        ArrayList<Line> rowSpanners = new ArrayList<Line>();
        boolean breaking = false;
//        System.out.println("---------------------------------------------------------");
        for(Line line : table){
            ArrayList<ArrayList<Element>> clusters = line.getClusters();
            int size = clusters.size();

//            System.out.println(size + " " + line);

            if(size <1 && foundData && breakingLine != null && doubleBreakingLine != null){
                break;               //then we have reached the end of the table.
            }
            else if(size <1 && foundData && breakingLine != null){
                doubleBreakingLine = line;
            }
            else if(size <1 && foundData && breakingLine==null){
                breakingLine = line;
            }
            else if(size < 2){                                          //Found no data, so should be above it.
                titleAndHeaders.add(line);
            }
            else if(breakingLine == null){
                data.add(line);     //Hooray, data!
                foundData = true;
            }
            else if(size > 1&&doubleBreakingLine != null && breakingLine != null){
                rowSpanners.add(breakingLine);
                rowSpanners.add(doubleBreakingLine);
                data.add(line);
                breakingLine = null;
                doubleBreakingLine = null;
            }
            else if(size>1 && breakingLine!= null){
                rowSpanners.add(breakingLine);
                data.add(line);
                breakingLine = null;
            }

        }
//        System.out.println("breaking line: " + breakingLine);
        this.titleAndHeaders = titleAndHeaders;
        this.data = data;
        this.rowSpanners = rowSpanners;
    }

    /**
     * This method finds lines that are above the line that was detected in the title and deletes those lines after giving
     * an error message.
     */
    private void filterLinesThatAreAboveY1(){
        ArrayList<Line> removedLines = new ArrayList<Line>();
        for (Line line : data){
            if(maxY1 > line.getY1OfFirstWord()||maxY1 > line.getY1OfLastWord()){
                LOGGER.info("Something is wrong, I detected the following line, which was above the title!");
                LOGGER.info(line.toString() + " " + maxY1 + " " + line.getY1OfFirstWord());
                removedLines.add(line);
            }
        }
        for(Line line : removedLines){
            data.remove(line);
        }
    }

    /**
    * This method removes the lines that have missing data and stores them in a separate variable.
    * These lines might contain valuable information about the content or could be a mistake by the OCR or separator.
    * They need special processing in order to be useful (as done in the addLinesWithMissingDataToColumns method).
    */
    private void findMissingData(){
        ArrayList<Line> dataWithoutMissingLines = new ArrayList<Line>();
        ArrayList<Line> linesWithMissingData = new ArrayList<Line>();
        ArrayList<Integer> numberOfClusters = new ArrayList<Integer>();
        int highestAmountOfClusters = 0;

        //calculating the highest amount of clusters:
        for(Line line : data){
            numberOfClusters.add(line.getClusterSize());
            if(line.getClusterSize() > highestAmountOfClusters){
                highestAmountOfClusters = line.getClusterSize();
            }
        }
        //calculate the highest amount of cluster occurrences:
        int highestAmountOfClustersOccurrences =0;
        ArrayList<Integer> numberOfClustersSave = new ArrayList<Integer>(numberOfClusters);
        while(numberOfClusters.contains(highestAmountOfClusters)){
            highestAmountOfClustersOccurrences++;
            numberOfClusters.remove(numberOfClusters.indexOf(highestAmountOfClusters));
        }
        numberOfClusters = new ArrayList<Integer>(numberOfClustersSave);
        validation.setHighestAmountOfClustersOccurrences(highestAmountOfClustersOccurrences);
        if(highestAmountOfClustersOccurrences > 4){
            int mostFrequentAmountOfClusters = CommonMethods.mostCommonElement(numberOfClusters);
            validation.setMostFrequentNumberOfClusters(mostFrequentAmountOfClusters);
            validation.setHighestAmountOfClusters(highestAmountOfClusters);
            for(Line line:data){
                if(line.getClusterSize() < highestAmountOfClusters){
                    linesWithMissingData.add(line);
                }
                else{
                    dataWithoutMissingLines.add(line);
                }
            }
            this.linesWithMissingData = linesWithMissingData;
            this.data = dataWithoutMissingLines;
        }
        else if(numberOfClusters.size()>0){
            int mostFrequentAmountOfClusters = CommonMethods.mostCommonElement(numberOfClusters);
            validation.setMostFrequentNumberOfClusters(mostFrequentAmountOfClusters);
            validation.setHighestAmountOfClusters(highestAmountOfClusters);
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
//            System.out.println("Lines without missing data: ");
//            for (Line line :  data){
//                System.out.println(line);
//            }
        }
    }

    /**
     * This method finds columns by taking the lines in the data variable and storing them in a map based on their partition
     * positions.
     */
    private void findColumns(){
        int counterForColumns = 0;
        Map<Integer, ArrayList<ArrayList<Element>>> columnMap = new HashMap<Integer, ArrayList<ArrayList<Element>>>();

        //Find highest amount of clusters:
        int highestAmountOfClusters = 0;
        for(Line line : data){
            if(line.getClusterSize() > highestAmountOfClusters){
                highestAmountOfClusters = line.getClusterSize();
            }
        }
//        for(Line line : data){
//            System.out.println(line.getClusterSize());
//        }
        for(Line line : data){
            for(ArrayList<Element>cluster : line.getClusters()){
                if(columnMap.containsKey(counterForColumns)){
                    ArrayList<ArrayList<Element>> fullClusters = columnMap.get(counterForColumns);
                    fullClusters.add(cluster);
                    columnMap.put(counterForColumns, fullClusters);
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
        System.out.println("columMap: ");
        System.out.println(dataByColumn);
    }

    /**
     * This method uses the map that was created in the findColumns method to create the Column objects
     * @param AVGCharDistance The Average character Distance as calculated in the Page class.
     */
    private void createColumns(double AVGCharDistance){
        ArrayList<Column2> dataInColumns = new ArrayList<Column2>();
        for(int key : dataByColumn.keySet()){
            Column2 column = new Column2(dataByColumn.get(key), AVGCharDistance, data);
            dataInColumns.add(column);
        }
        this.dataInColumns = dataInColumns;
    }

    /**
     * This method tries to check the columns. If there are any cells that don't fit in the column, they are moved to another
     * column or create a new column.
     */
    private void checkColumns(){
        for(Column2 column : dataInColumns){
            if(!column.checkFirstThreeCells()){
                this.missPlacedCells = column.getWrongCells();
            }
        }
    }

    private void replaceMissPlacedCells(){
        for(ArrayList<Element> cluster : missPlacedCells){
            for(Column2 column : dataInColumns){
                if(column.fitsInColumn(Line.getClusterX1(cluster), Line.getClusterX2(cluster)) &&
                        column.columnFitsIn(Line.getClusterX1(cluster), Line.getClusterX2(cluster))){
                    //then we need to add this cluster to that column:
                    column.addCell(cluster);
                }
                else if(column.fitsInColumn(Line.getClusterX1(cluster), Line.getClusterX2(cluster)) ||
                        column.columnFitsIn(Line.getClusterX1(cluster), Line.getClusterX2(cluster))){
                    //then we need to add this cluster to that column:
                    column.addCell(cluster);
                }
                else if(column.touchesColumn(Line.getClusterX1(cluster), Line.getClusterX2(cluster))){
//                    System.out.println("CLUSTER: " + cluster + " touches: " + column);
                    column.addCell(cluster);
                }
            }
        }
    }

    /**
     * This method adds lines with missing partitions to the current columns.
     * It loops trough lines that were flagged for containing missing data and then adds the ones to columns that they
     * fit in (or to columns that fit in the partition). If this fails it will also try to check if the partition merely
     * touches a column, although this will return a lower validation score if successful.
     */
    private void addLinesWithMissingDataToColumns(){
        ArrayList<Cell> cellsWithMissingDataAdded = new ArrayList<Cell>();
        ArrayList<Column2> newDataInColumns = new ArrayList<Column2>();
        for(Line line: linesWithMissingData){
            ArrayList<ArrayList<Element>> clusters = line.getClusters();
            for(ArrayList<Element> cluster : clusters){
                for(Column2 column : dataInColumns){
                    if(column.fitsInColumn(Line.getClusterX1(cluster), Line.getClusterX2(cluster)) &&
                            column.columnFitsIn(Line.getClusterX1(cluster), Line.getClusterX2(cluster))){
                        //then we need to add this cluster to that column:
                        newDataInColumns.remove(column);
                        column.addCell(cluster);
                        newDataInColumns.add(column);
                        Cell cell = new Cell(cluster, 3, line.getLineNumber());
                        cellsWithMissingDataAdded.add(cell);
                    }
                    else if(column.fitsInColumn(Line.getClusterX1(cluster), Line.getClusterX2(cluster)) ||
                            column.columnFitsIn(Line.getClusterX1(cluster), Line.getClusterX2(cluster))){
                        //then we need to add this cluster to that column:
                        newDataInColumns.remove(column);
                        column.addCell(cluster);
                        newDataInColumns.add(column);
                        Cell cell = new Cell(cluster, 2, line.getLineNumber());
                        cellsWithMissingDataAdded.add(cell);
                    }
                    else if(column.touchesColumn(Line.getClusterX1(cluster), Line.getClusterX2(cluster))){
//                        System.out.println("CLUSTER: " + cluster + " touches: " + column);
                        newDataInColumns.remove(column);
                        column.addCell(cluster);
                        newDataInColumns.add(column);
                        Cell cell = new Cell(cluster, 1, line.getLineNumber());
                        cellsWithMissingDataAdded.add(cell);
                    }
                }
            }
        }
        validation.setCellsWithMissingDataAdded(cellsWithMissingDataAdded.size());
        validation.setCellsWithMissingDataAddedScores(cellsWithMissingDataAdded);
    }

    /**
     * This method finds the average distances between the partitions and parses those to the validation object for the calculation
     * of the column confidence.
     */
    private void setClusterCertainties(){
        method:
        while (true){
            ArrayList<Integer> totalDistances = data.get(0).getDistances();
            for(Line line : data){
                if(data.indexOf(line) >0){
                    for(int x =0;x<line.getDistances().size();x++){
                        if(!(x >= totalDistances.size()||x>=line.getDistances().size())){
                            int totalDistance =  totalDistances.get(x) + line.getDistances().get(x);
                            totalDistances.set(x, totalDistance);
                        }
                        else{
                            LOGGER.info("Found a problem during the cluster certainties. I've given the table a very low confidence");
                            ArrayList<Integer> lowValidation = new ArrayList<Integer>();
                            for(int o : line.getDistances()){
                                lowValidation.add(0);
                            }
                            validation.setClusterCertainty(lowValidation, data.get(0).getDistanceThreshold());
                            validation.setLineThreshold(data.get(0).getDistanceThreshold());
                            break method;
                        }
                    }
                }
            }
            ArrayList<Integer> averageDistances = new ArrayList<Integer>();
            for(int distance : totalDistances){
                averageDistances.add(distance/data.size());
            }
            validation.setClusterCertainty(averageDistances, data.get(0).getDistanceThreshold());
            validation.setLineThreshold(data.get(0).getDistanceThreshold());
            break method;
        }
    }

    /**
     * This method checks if the table itself is a false positive and should be flagged.
     */
    public void checkForFalsePositive(){
        int almostEmptyColumns = 0;
        for(Column2 column : dataInColumns){
            if(column.getNumberOfCells() == 1){
                almostEmptyColumns+=1;
            }
        }
        if(almostEmptyColumns >=2){
            validation.setFalsePositive(true);
        }
    }

    /**
     * This method is used for extraction of tables with lot of empty cells in it. It is required for the successful extraction of
     * most Matrix tables.
     */
    private void fillBlankCells(){
        //We say: cells get a line number. If a column does not contain a cell on a certain line, add a whitespace.
        //Any cell that is not filled must be empty:
        for(Line line : data){
            int lineNumber = line.getLineNumber();
            COLUMNLOOP:
            for(Column2 column : dataInColumns){
                for(Cell cell : column.getCellObjects()){
                    if(cell.getLineNumber() == lineNumber){
                        break;
                    }
                    if(cell.getLineNumber()>line.getLineNumber()){                //the last cell?
                        //Add a blank cell to this column.
//                        System.out.println("Add line to :" + column + " in line: " + line.getLineNumber());

                        //<span class='ocrx_word' id='word_9' title="bbox 2175 514 2346 555">were</span>

                        Tag t = Tag.valueOf("span");
                        Attributes attributes = new Attributes();
                        attributes.put("class", "ocrx_word");
                        attributes.put("id", "word_ADDEDBYTEA");
                        attributes.put("title", "bbox " + column.getAverageX1() + " " + (int) line.getAverageY1() + " " + column.getAverageX2() + " " + (int) line.getAverageY2());

                        Element newElement = new Element(t, "localhost:8080", attributes);
                        newElement.text(" ");
                        ArrayList<Element> newCell = new ArrayList<Element>();
                        newCell.add(newElement);
//                        System.out.println("adding: " +newElement.text());
                        column.addCell(newCell);
                        break COLUMNLOOP;
                    }
                }
            }
        }
    }

    /**
     * This method calculates the X1, X2, Y1 and Y2 values of the table.
     */
    private void setTableBoundaries(SemanticFramework semanticFramework){
        this.minY1 = semanticFramework.getTitle().get(0).getLowestY1();

        int maxY2 = Integer.MIN_VALUE;
        int y2 = 0;
        for(Column2 column : dataInColumns){
            y2 = column.getColumnBoundaryY2();
            if(y2 > maxY2 ){
                maxY2 = y2;
            }
        }
        this.maxY2 = y2;
        this.minX1 = dataInColumns.get(0).getColumnBoundaryX1();
        this.maxX2 = dataInColumns.get(dataInColumns.size()-1).getColumnBoundaryX2();
    }

    //This method will try to recreate the original lines of the table using all the previous information about the table.
    private ArrayList<ArrayList<Cell>> recreateTableLines(SemanticFramework semanticFramework){
        ArrayList<ArrayList<Cell>> table = new ArrayList<ArrayList<Cell>>();
        for(Column2 column : dataInColumns){
            ArrayList<Cell> cells = column.getCellObjects();
            table.add(cells);
        }
        int maxLength = Integer.MIN_VALUE;
        for(ArrayList<Cell> column : table){
            int length = column.size();
            if(length > maxLength){
                maxLength = length;
            }
        }
        ArrayList<ArrayList<Cell>> newTable = new ArrayList<ArrayList<Cell>>();
        ArrayList<Column2> incompleteRowColumns = new ArrayList<Column2>();
        ArrayList<Cell> col = new ArrayList<Cell>();

        int counter = 0;
        while(counter < maxLength){
            ArrayList<Cell> line = new ArrayList<Cell>();
            newTable.add(line);
            counter++;
        }

        for(Column2 column : dataInColumns){
            if(column.getCellObjects().size()==maxLength){
                col = column.getCellObjects();
                for(ArrayList<Cell> line : newTable){
                    if(line.size()>= dataInColumns.indexOf(column)){
                        line.add(dataInColumns.indexOf(column),col.get(newTable.indexOf(line)));
                    }
                    else{
                        line.add(col.get(newTable.indexOf(line)));
                    }
                }
            }
            else{
                incompleteRowColumns.add(column);         //gonna be mapped
            }
        }
        for(Column2 column : incompleteRowColumns){
            ArrayList<Cell> cells = column.getCellObjects();
            for(Cell cellOfNewColumn : cells){
                System.out.println(cellOfNewColumn);
                boolean breaking = false;
                Cells:
                for(ArrayList<Cell> line : newTable){
                    for(Cell cell : line){
                        if(cell.getY1() >= cellOfNewColumn.getY2()){        //||(CommonMethods.calcDistance(cell.getY2(), cellOfNewColumn.getY1())>(averageLineDistance*verticalThresholdModifier))&&line.indexOf(cellOfNewColumn)!=0)
                            System.out.println(newTable.indexOf(line) + " " + cell + " " + cellOfNewColumn + " " + newTable.size() + " " + dataInColumns.indexOf(column) + " " + line.size());
                            if(newTable.indexOf(line)!=0){
                                System.out.println(newTable.get(newTable.indexOf(line)-1).size()+" " +dataInColumns.indexOf(column));
                                if(newTable.get(newTable.indexOf(line)-1).size() >= dataInColumns.indexOf(column)){
                                    newTable.get(newTable.indexOf(line)-1).add(dataInColumns.indexOf(column),cellOfNewColumn);
                                    breaking = true;
                                    break Cells;
                                }
                                else{
                                    newTable.get(newTable.indexOf(line)-1).add(cellOfNewColumn);
                                    breaking = true;
                                    break Cells;
                                }
                            }
                            else{
                                newTable.add(0, new ArrayList<Cell>());
                                newTable.get(0).add(cellOfNewColumn);
                                breaking = true;
                                break Cells;
                            }
                        }
                    }

                }
                if(!breaking){
                    System.out.println("wasn't breaking. Now it is!");
                    System.out.println(newTable.size() + " " + dataInColumns.indexOf(column) + " " + newTable.get(newTable.size()-1).size());
                    System.out.println(dataInColumns.indexOf(column) + " "+ (newTable.size()-1));
                    if(dataInColumns.indexOf(column)<=newTable.get(newTable.size()-1).size()-1){
                        newTable.get(newTable.size()-1).add(dataInColumns.indexOf(column),cellOfNewColumn);
                        breaking = true;
                    }
                    else{
                        newTable.get(newTable.size()-1).add(cellOfNewColumn);
                        breaking = true;
                    }
                }
            }
        }

        //TODO: Implement missing data by looking at the touchcolumn of the rowspan and add all the columns from data in columns as empty cells.
        double totalY2 = 0;
        double averageY2;
        for(Line rowSpanner : semanticFramework.getValidatedRowSpanners()){
            for(ArrayList<Cell> line : newTable){
                for(Cell cell : line){
                    totalY2 = totalY2 + cell.getY2();
                }
                averageY2 = totalY2 / line.size();
                if(rowSpanner.getAverageY1() < averageY2){
                        newTable.add(newTable.indexOf(line), new ArrayList<Cell>());
                        newTable.get(newTable.indexOf(line)-1).add(rowSpanner.getCellObject());
                        break;
                }
            }
        }

        for(Line subHeader : semanticFramework.getRowSpanners()){
            for(ArrayList<Cell> line : newTable){
                for(Cell cell : line){
                    totalY2 = totalY2 + cell.getY2();
                }
                int firstMappedColumn = 0;
                averageY2 = totalY2 / line.size();
                if(subHeader.getAverageY1() < averageY2){
                    newTable.add(newTable.indexOf(line), new ArrayList<Cell>());
                    for(Column2 column : dataInColumns){
                        newTable.get(newTable.indexOf(line)-1).add(new Cell());
                        if(column.touchesColumn(subHeader.getFirstX1(), subHeader.getLastX2())){
                            firstMappedColumn = dataInColumns.indexOf(column);
                        }
                    }
                    newTable.get(newTable.indexOf(line)-1).set(firstMappedColumn,subHeader.getCellObject());
                    break;
                }
            }

        }

        System.out.println("NEWTABLE:");
        System.out.println(newTable);
        return newTable;
    }

    /*
    ( y1>lastY2 || CommonMethods.calcDistance(lastY2, y1)>(averageLineDistance*verticalThresholdModifier))
                    &&spans.indexOf(span)!=0
     */

    /**
     * This method writes the collected debugContent to a debug file.
     * @param content A string containing the collected content
     * @param location The location for the method to write to.
     * @param file The location of the used file, for naming purpose.
     * @throws IOException When an incorrect path has been given.
     */
    private void writeDebugFile(String content, String location, File file) throws IOException {
        LOGGER.info("Writing results to file: " + location + "\\results\\" + file.getName().substring(0, file.getName().length() - 5)+ ".txt");
        FileWriter fileWriter;
        String writeLocation = location + "\\results\\" + file.getName().substring(0, file.getName().length() - 5)+ ".txt";
        File newTextFile = new File(writeLocation);
        fileWriter = new FileWriter(newTextFile);
        fileWriter.write(content);
        fileWriter.close();
    }

    /**
     * This is the new write method that uses XML methods to generate an XML file.
     * @param location The location where the new file should be stored.
     * @param file The file that was being used to create this table.
     * @param tableID The ID of the table.
     * @param semanticFramework The semantic framework object. This contains get methods that are required for the output
     * File.
     * @throws IOException If the given location doesn't exist.
     */
    private void write2(String location,File file ,int tableID, SemanticFramework semanticFramework) throws IOException {
        try{
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            org.w3c.dom.Element rootElement = doc.createElement("TEAFile");
            doc.appendChild(rootElement);

            //provenance:
            org.w3c.dom.Element provenance = doc.createElement("provenance");
            rootElement.appendChild(provenance);

            org.w3c.dom.Element detectionID = doc.createElement("DetectionID");
            detectionID.appendChild(doc.createTextNode(tableID + ""));
            provenance.appendChild(detectionID);
            org.w3c.dom.Element fromFile = doc.createElement("fromFile");
            fromFile.appendChild(doc.createTextNode(file.getName()));
            provenance.appendChild(fromFile);

            org.w3c.dom.Element fromPath = doc.createElement("fromPath");
            fromPath.appendChild(doc.createTextNode(file.getAbsolutePath()));
            provenance.appendChild(fromPath);
            org.w3c.dom.Element horizontalThresholdModifier = doc.createElement("horizontalThresholdModifier");
            horizontalThresholdModifier.appendChild(doc.createTextNode(this.horizontalThresholdModifier + ""));
            provenance.appendChild(horizontalThresholdModifier);
            org.w3c.dom.Element verticalThresholdModifier = doc.createElement("horizontalThresholdModifier");
            verticalThresholdModifier.appendChild(doc.createTextNode(this.verticalThresholdModifier + ""));
            provenance.appendChild(verticalThresholdModifier);

            //Results:
            org.w3c.dom.Element results = doc.createElement("results");
            rootElement.appendChild(results);

            org.w3c.dom.Element minX1 = doc.createElement("TableBoundaryX1");
            minX1.appendChild(doc.createTextNode(this.minX1+""));
            results.appendChild(minX1);
            org.w3c.dom.Element maxX2 = doc.createElement("TableBoundaryX2");
            maxX2.appendChild(doc.createTextNode(this.maxX2+""));
            results.appendChild(maxX2);
            org.w3c.dom.Element minY1 = doc.createElement("TableBoundaryY1");
            minY1.appendChild(doc.createTextNode(this.minY1 + ""));
            results.appendChild(minY1);
            org.w3c.dom.Element maxY2 = doc.createElement("TableBoundaryY2");
            maxY2.appendChild(doc.createTextNode(this.maxY2 + ""));
            results.appendChild(maxY2);


            org.w3c.dom.Element title1 = doc.createElement("title1");
            title1.appendChild(doc.createTextNode(name));
            results.appendChild(title1);
            org.w3c.dom.Element title2 = doc.createElement("title2");
            title2.appendChild(doc.createTextNode(titleAndHeaders.toString()));
            results.appendChild(title2);

            org.w3c.dom.Element columns = doc.createElement("columns");
            results.appendChild(columns);

            for(Column2 columnContent : dataInColumns){
                org.w3c.dom.Element column = doc.createElement("column");
                column.appendChild(doc.createTextNode(columnContent.toString().replace("�", "")));
                columns.appendChild(column);
            }

            org.w3c.dom.Element lines = doc.createElement("lines");
            results.appendChild(lines);

            ArrayList<ArrayList<Cell>> table = recreateTableLines(semanticFramework);

            for(ArrayList<Cell> line : table){
                org.w3c.dom.Element XMLLine = doc.createElement("line");
                XMLLine.appendChild(doc.createTextNode(line.toString().replace("�", "")));
                lines.appendChild(XMLLine);
            }

            /*System.out.println("table:" + table);

            for(Line line : data){
                if(line.getHighestY2()<=this.maxY2&&line.getLowestY1()>=this.minY1&&line.getClusterSize()>1){
                    org.w3c.dom.Element XMLLine = doc.createElement("line");
                    XMLLine.appendChild(doc.createTextNode(line.toString().replace("�", "")));
                    lines.appendChild(XMLLine);
                }
                else{
                    System.out.println("NO GOOD: " + line);
                }
            }
*/
            if(rowSpanners.size() > 0){
                org.w3c.dom.Element rowSpanners = doc.createElement("rowSpanners");
                results.appendChild(rowSpanners);
                for(Line line : this.rowSpanners){
                    org.w3c.dom.Element rowSpanner = doc.createElement("rowSpanner");
                    rowSpanner.appendChild(doc.createTextNode(line.toString()));
                    rowSpanners.appendChild(rowSpanner);
                }
            }

            //Semantics:
            org.w3c.dom.Element semantics = doc.createElement("tableSemantics");
            rootElement.appendChild(semantics);
            org.w3c.dom.Element title = doc.createElement("title");
            title.appendChild(doc.createTextNode(semanticFramework.getTitle().toString()));
            semantics.appendChild(title);
            org.w3c.dom.Element titleConfidence = doc.createElement("titleConfidence");
            Double semanticFrameworkDouble = semanticFramework.getTitleConfidence();
            titleConfidence.appendChild(doc.createTextNode(semanticFrameworkDouble.toString()));
            semantics.appendChild(titleConfidence);

            if(!semanticFramework.getRowSpanners().isEmpty()){
                org.w3c.dom.Element rowSpanners = doc.createElement("subHeaders");
                rowSpanners.appendChild(doc.createTextNode(semanticFramework.getRowSpanners().toString()));
                semantics.appendChild(rowSpanners);
                org.w3c.dom.Element IdentifiersConfidenceAlignment = doc.createElement("subHeadersConfidenceAlignment");
                IdentifiersConfidenceAlignment.appendChild(doc.createTextNode(semanticFramework.getIdentifiersConfidenceAlignment().toString()));
                semantics.appendChild(IdentifiersConfidenceAlignment);
                org.w3c.dom.Element getIdentifiersConfidenceColumnsSpanned = doc.createElement("subHeadersConfidenceColumnsSpanned");
                getIdentifiersConfidenceColumnsSpanned.appendChild(doc.createTextNode(semanticFramework.getIdentifiersConfidenceColumnsSpanned().toString()));
                semantics.appendChild(getIdentifiersConfidenceColumnsSpanned);
                org.w3c.dom.Element IdentifiersConfidenceLineDistance= doc.createElement("subHeadersConfidenceLineDistance");
                IdentifiersConfidenceLineDistance.appendChild(doc.createTextNode(semanticFramework.getIdentifiersConfidenceLineDistance().toString()));
                semantics.appendChild(IdentifiersConfidenceLineDistance);
            }
            if(!semanticFramework.getValidatedRowSpanners().isEmpty()){
                org.w3c.dom.Element rowSpanners = doc.createElement("rowSpanners");
                rowSpanners.appendChild(doc.createTextNode(semanticFramework.getValidatedRowSpanners().toString()));
                semantics.appendChild(rowSpanners);
                org.w3c.dom.Element rowSpannersConfidenceAlignment = doc.createElement("rowSpannersConfidenceAlignment");
                rowSpannersConfidenceAlignment.appendChild(doc.createTextNode(semanticFramework.getRowSpannersConfidenceAlignment().toString()));
                semantics.appendChild(rowSpannersConfidenceAlignment);
                org.w3c.dom.Element rowSpannersConfidenceColumnsSpanned = doc.createElement("rowSpannersConfidenceColumnsSpanned");
                rowSpannersConfidenceColumnsSpanned.appendChild(doc.createTextNode(semanticFramework.getRowSpannersConfidenceColumnsSpanned().toString()));
                semantics.appendChild(rowSpannersConfidenceColumnsSpanned);
                org.w3c.dom.Element rowSpannersConfidenceLineDistance = doc.createElement("rowSpannersConfidenceLineDistance");
                rowSpannersConfidenceLineDistance.appendChild(doc.createTextNode(semanticFramework.getRowSpannersConfidenceLineDistance().toString()));
                semantics.appendChild(rowSpannersConfidenceLineDistance);
            }
            org.w3c.dom.Element headers = doc.createElement("headers");
            headers.appendChild(doc.createTextNode(semanticFramework.getHeaders().toString()));
            semantics.appendChild(headers);
            org.w3c.dom.Element headersConfidence = doc.createElement("headersConfidence");
            headersConfidence.appendChild(doc.createTextNode(semanticFramework.getHeaderConfidence().toString()));
            semantics.appendChild(headersConfidence);

            //validation:
            org.w3c.dom.Element validation = doc.createElement("validation");
            rootElement.appendChild(validation);

            org.w3c.dom.Element clusterCertainty = doc.createElement("columnConfidence");
            clusterCertainty.appendChild(doc.createTextNode(this.validation.getClusterCertainty().toString()));
            validation.appendChild(clusterCertainty);
            org.w3c.dom.Element mostFrequentNumberOfClusters = doc.createElement("mostFrequentNumberOfClusters");
            mostFrequentNumberOfClusters.appendChild(doc.createTextNode(this.validation.getMostFrequentNumberOfClusters()+""));
            validation.appendChild(mostFrequentNumberOfClusters);
            org.w3c.dom.Element highestAmountOfClusters = doc.createElement("highestAmountOfClusters");
            highestAmountOfClusters.appendChild(doc.createTextNode(this.validation.getHighestAmountOfClusters()+""));
            validation.appendChild(highestAmountOfClusters);
            org.w3c.dom.Element highestAmountOfClustersOccurrences = doc.createElement("highestAmountOfClustersOccurrences");
            highestAmountOfClustersOccurrences.appendChild(doc.createTextNode(this.validation.getHighestAmountOfClustersOccurrences()+""));
            validation.appendChild(highestAmountOfClustersOccurrences);
            org.w3c.dom.Element clusterThreshold = doc.createElement("clusterThreshold");
            clusterThreshold.appendChild(doc.createTextNode(this.validation.getLineThreshold()+""));
            validation.appendChild(clusterThreshold);
            org.w3c.dom.Element cellsWithMissingDataAdded = doc.createElement("cellsWithMissingDataAdded");
            cellsWithMissingDataAdded.appendChild(doc.createTextNode(this.validation.getCellsWithMissingDataAdded()+""));
            validation.appendChild(cellsWithMissingDataAdded);
            if(this.validation.getCellsWithMissingDataAdded() > 0){
                org.w3c.dom.Element cellsWithMissingDataAddedScores = doc.createElement("cellsWithMissingDataAddedScores");
                cellsWithMissingDataAddedScores.appendChild(doc.createTextNode(this.validation.getCellsWithMissingDataAddedObjects()+""));
                validation.appendChild(cellsWithMissingDataAddedScores);
            }
            org.w3c.dom.Element averageDistanceBetweenRows = doc.createElement("averageDistanceBetweenRows");
            averageDistanceBetweenRows.appendChild(doc.createTextNode(this.validation.getAverageDistanceBetweenRows()+""));
            validation.appendChild(averageDistanceBetweenRows);
            if(this.validation.getTitleConfidence().size() > 0){
                org.w3c.dom.Element TitleConfidence = doc.createElement("TitleConfidence");
                TitleConfidence.appendChild(doc.createTextNode(this.validation.getTitleConfidence()+""));
                validation.appendChild(TitleConfidence);
            }
            org.w3c.dom.Element falsePositive = doc.createElement("falsePositive");
            falsePositive.appendChild(doc.createTextNode(this.validation.getFalsePositive()+""));
            validation.appendChild(falsePositive);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            LOGGER.info("Written file: " + location + "\\results\\" + file.getName().substring(0, file.getName().length() - 5) + "-" + tableID+ ".xml");
            File file2 = new File(location + "\\results\\" + file.getName().substring(0, file.getName().length() - 5) + "-" + tableID+ ".xml");
            Writer output = new BufferedWriter(new FileWriter(file2));
            StreamResult result = new StreamResult(output);

            // Output to console for testing
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(source, result);
            output.close();
            System.out.println("File saved.");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method returns the table right after it finished reading lines. No column indication has been made
     * @return A String containing the initial lines of the table.
     */
    public String getRawTable(){
        String rawTable = "";
        rawTable = rawTable + ("~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-");
        rawTable = rawTable + ("RAWData in this table is: \n");
        for(Line line : data){
            rawTable = rawTable + (line);
        }
        LOGGER.info(rawTable);
        return rawTable;
    }

    /**
     * This method returns the name of the table.
     * @return A string containing the name of the table
     */
    public String getName(){
        return name;
    }
}
