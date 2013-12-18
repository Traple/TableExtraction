package program7;

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
     * @throws IOException
     */
    public Table2(Elements spans, double charLengthThreshold, File file, String workspace, int tableID, double verticalThresholdModifier, double horizontalThresholdModifier, double averageLineDistance, boolean debugging) throws IOException {
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
//            System.out.println(table);
            separateDataByCluster();
            filterLinesThatAreAboveY1();

        if(data.size()>1){
            System.out.println(getRawTable());
            debugContent = debugContent + getRawTable()+ "\n";
            filterEmptyLines();
            findMissingData();
            findColumns();
            createColumns(charLengthThreshold);
            debugContent = debugContent + "lines with missing data: " + linesWithMissingData + "\n";
            if(linesWithMissingData!=null){
                addLinesWithMissingDataToColumns();
            }
//            checkTheTitle();
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
            LOGGER.info("Table2: " + getName());
            System.out.println("In Table2: " + getName());
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
            SemanticFramework semanticFramework = new SemanticFramework(dataInColumns, (averageLineDistance * verticalThresholdModifier), rowSpanners, charLengthThreshold * horizontalThresholdModifier, table, validation, titleAndHeaders);
            System.out.println(semanticFramework);
            write(getXMLContent(file, tableID, semanticFramework.getXML()), (workspace) ,file, tableID);
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

        for(Element span : spans){
            pos = span.attr("title");
            positions = pos.split("\\s+");
            int x1 = Integer.parseInt(positions[1]);
            int y1 = Integer.parseInt(positions[2]);
            int x2 = Integer.parseInt(positions[3]);
            int y2 = Integer.parseInt(positions[4]);

//            System.out.println(span);
//            System.out.println((CommonMethods.calcDistance(lastY2, y1)) + " " + ((averageLineDistance*verticalThresholdModifier)/1.5) + " " +(CommonMethods.calcDistance(lastY2, y1)>(averageLineDistance*verticalThresholdModifier)/2));
//            System.out.println((x1>=lastX2) );

            if(((!(x1>=lastX2))||y1>lastY2 || CommonMethods.calcDistance(lastY2, y1)>(averageLineDistance*verticalThresholdModifier)/1.5)&&spans.indexOf(span)!=0){
                Line line = new Line(currentLine, charLengthThreshold, horizontalThresholdModifier);
//                System.out.println(line);
//                System.out.println(line.getClusterSize());

                table.add(line);
                currentLine = new Elements();
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
        for(Line line : table){
            ArrayList<ArrayList<Element>> clusters = line.getClusters();
            int size = clusters.size();

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
        System.out.println("breaking line: " + breakingLine);
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
                LOGGER.info(maxY1 + " " + line.getY1OfFirstWord());
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

        for(Line line : data){
            numberOfClusters.add(line.getClusterSize());
            if(line.getClusterSize() > highestAmountOfClusters){
                highestAmountOfClusters = line.getClusterSize();
            }
        }
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
        }
    }

    /**
     * This method finds columns by taking the lines in the data variable and storing them in a map based on their partition
     * positions.
     */
    private void findColumns(){
        int counterForColumns = 0;
        Map<Integer, ArrayList<ArrayList<Element>>> columnMap = new HashMap<Integer, ArrayList<ArrayList<Element>>>();

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
    }

    /**
     * This method uses the map that was created in the findColumns method to create the Column objects
     * @param AVGCharDistance The Average character Distance as calculated in the Page class.
     */
    private void createColumns(double AVGCharDistance){
        ArrayList<Column2> dataInColumns = new ArrayList<Column2>();
        for(int key : dataByColumn.keySet()){
            Column2 column = new Column2(dataByColumn.get(key), AVGCharDistance);
            dataInColumns.add(column);
        }
        this.dataInColumns = dataInColumns;
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
                        Cell cell = new Cell(cluster, 3);
                        cellsWithMissingDataAdded.add(cell);
                    }
                    else if(column.fitsInColumn(Line.getClusterX1(cluster), Line.getClusterX2(cluster)) ||
                            column.columnFitsIn(Line.getClusterX1(cluster), Line.getClusterX2(cluster))){
                        //then we need to add this cluster to that column:
                        newDataInColumns.remove(column);
                        column.addCell(cluster);
                        newDataInColumns.add(column);
                        Cell cell = new Cell(cluster, 2);
                        cellsWithMissingDataAdded.add(cell);
                    }
                    else if(column.touchesColumn(Line.getClusterX1(cluster), Line.getClusterX2(cluster))){
                        System.out.println("CLUSTER: " + cluster + " touches: " + column);
                        newDataInColumns.remove(column);
                        column.addCell(cluster);
                        newDataInColumns.add(column);
                        Cell cell = new Cell(cluster, 1);
                        cellsWithMissingDataAdded.add(cell);
                    }
                }
            }
        }
        validation.setCellsWithMissingDataAdded(cellsWithMissingDataAdded.size());
        validation.setCellsWithMissingDataAddedScores(cellsWithMissingDataAdded);
    }

    //TODO: Test this method a bit more.

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
     * This method returns the results of the extraction of the table and puts them in a XML format.
     * @param file This is the File which was used for the extraction of the Table
     * @param tableID This is the ID of the table that was extracted
     * @param semanticXML The results from the semantic framework in XML
     * @return This method returns a String containing the results of the Table extraction in valid XML.
     */
    private String getXMLContent(File file, int tableID, String semanticXML){
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
        fileContent = fileContent + semanticXML;
        fileContent = fileContent + validation.toXML();
        fileContent = fileContent + "</TEAFile>";
        return fileContent;
    }

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
     * This method writes the results to the results directory in the workspace. Output is in XML.
     * @param filecontent The results as being collected during the reconstruction of this table.
     * @param location The path to the the XML file (output).
     * @param file The file which was used to reconstruct this table. (used for provenance and file-naming purpose)
     * @param tableID The ID of the table to make the path unique.
     * @throws java.io.IOException
     */
    private void write(String filecontent, String location, File file, int tableID) throws IOException {
        LOGGER.info("Writing results to file: " + location + "/" + file.getName().substring(0, file.getName().length() - 5) + "-" + tableID + ".xml");
        FileWriter fileWriter;
        String writeLocation = location + "/results/" + file.getName().substring(0, file.getName().length() - 5) + "-" + tableID+ ".xml";
        File newTextFile = new File(writeLocation);
        fileWriter = new FileWriter(newTextFile);
        fileWriter.write(filecontent);
        fileWriter.close();
    }

    /**
     * This method creates the provenance that is being used for writing the output.
     * @param file The file which was used to create this table.
     * @param tableID The unique ID of the table.
     * @return A string containing the provenance in XML format.
     */
    private String getProvenance(File file, int tableID){
        return "    <provenane>\n"+
                "        <fromFile>" + file.getName()+"</fromFile>\n" +
                "        <fromPath>" + file.getAbsolutePath() +"</fromPath>\n"+
                "        <user>Sander</user>\n"+
                "        <detectionID>" + tableID +"</detectionID>\n"+
                "        <usedHorizontalThresholdModifier>"+horizontalThresholdModifier+"</usedHorizontalThresholdModifier>\n"+
                "        <usedVerticalThresholdModifier>"+verticalThresholdModifier+"</usedVerticalThresholdModifier>\n"+
                "    </provenance>\n";
    }

    /**
     * This method returns the table right after it finished reading lines. No column indication has been made
     * @return A String containing the initial lines of the table.
     */
    public String getRawTable(){
        String rawTable = "";
        rawTable = rawTable + ("~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-");
        rawTable = rawTable + ("RAWData in this table is: ");
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
