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

    /**
     * This is the constructor of the table class. It takes it's parameters and sets them as local variables.
     * It also puts the default values for the rest of the table and then starts calling the other methods in this class.
     * to extract the table according to the rules of TEA.
     * @param spans These are the words below the table detection from the Page class.
     * @param charLengthThreshold This is the character length threshold as calculated in the Page class.
     * @param file This is the File that was used to extract the table from. It is only used for the creation of provenance.
     * @param workspace This is the workspace as specified by the user.
     * @param tableID This is the ID of the detected table. It is mainly used for the creation of the output file and for provenance.
     * @throws IOException
     */
    public Table2(Elements spans, double charLengthThreshold, File file, String workspace, int tableID) throws IOException {
        this.maxY1 = 0;
        this.spans = spans;
        this.name = "";
        this.lineDistanceModifier = 1.0;

        this.validation = new Validation();
        if(spans.size() > 0){
        setMaxY1();
        this.table = new ArrayList<Line>();
        createLines(charLengthThreshold);

        separateDataByCluster();
        filterLinesThatAreAboveY1();

        if(data.size()>1){
            System.out.println(data.size());
            System.out.println(data);
            System.out.println("~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-");
            System.out.println("RAWData in this table is: ");
            for(Line line : data){
                validation.setClusterCertainty(line.getDistances(), line.getDistanceThreshold());
                validation.setLineThreshold(line.getDistanceThreshold());
                System.out.println(line);
            }
            filterEmptyLines();
            findMissingData();
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
           write(getXMLContent(file, tableID), (workspace+"/results") ,file, tableID);
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

    //TODO: USE THE Y values to calculate the rows as well as their X positions.
    /**
     * This method creates the lines by reading trough the file.
     * @param charLengthThreshold This is the average characterLength as calculated in the Page class.
     */
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

    //TODO: Add the highest amount of clusters as well.
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

    /**
     * This method checks if the last sentence of the title should be in the headers.
     * The method does this by looking at the last line of the title and checking if there is more space between this line
     * and the data and this line and the rest of the title. If there is more space between the last line of the title and
     * the rest of the title the method assumes that this is in fact a single header (with no partitions).
     * The validation is called to show how much distance was between the title and the data.
     */
    private void checkTheTitle(){
        findAverageLineDistance();
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
            }
        }
    }

    /**
     * This method finds the average distance between rows and stores this in the private averageLineDistance variable.
     * It is also send to the validation class for validation purposes.
     */
    private void findAverageLineDistance(){
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

    /**
     * This method returns the results of the extraction of the table and puts them in a XML format.
     * @param file This is the File which was used for the extraction of the Table
     * @param tableID This is the ID of the table that was extracted
     * @return This method returns a String containing the results of the Table extraction in valid XML.
     */
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
        location = location + "\\" + file.getName() + "-" + tableID+ ".xml";
        File newTextFile = new File(location);
        fileWriter = new FileWriter(newTextFile);
        fileWriter.write(filecontent);
        fileWriter.close();
    }

    //TODO: Add the used parameters in the provenance!
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
