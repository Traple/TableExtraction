package program7;

import java.util.*;
import java.util.logging.Logger;

/**
 * This class will provide a semantic framework for the table. This consists of the following points:
 * - Title
 * - Headers
 * - Data
 * - Possible Rowspans/Identifiers
 * - Possible Columnspans
 *
 * Steps:
 * - Find indentation (to the left or to the right?)
 * - Link indentation with rowspans
 * - Find distance between lines and link it to rowspans
 * - Find if rowspans span multiple columns and if they start at the first column
 * - Find headers
 * - Check added rowspans if their not a header.
 *
 * Every point should contain their expected value and a validation score
 */
public class SemanticFramework {

    public static Logger LOGGER = Logger.getLogger(SemanticFramework.class.getName());
    private ArrayList<Column2> table = new ArrayList<Column2>();
    private ArrayList<Line> rawTable;
    private double verticalHeightThreshold;
    private ArrayList<Line> rowSpanners;
    private double horizontalLengthThreshold;
    private ArrayList<Double> listOfBaseX1;
    private ArrayList<Double> identifiersConfidenceAlignment;
    private ArrayList<Integer> identifiersConfidenceColumnsSpanned;
    private ArrayList<Double> identifiersConfidenceLineDistance;
    private ArrayList<Line> title;
    private ArrayList<Line> headers;
    private ArrayList<Double> headerConfidence;
    private double titleConfidence;
    private ArrayList<Line> validatedRowSpanners;
    private ArrayList<Double> rowSpannersConfidenceAlignment;
    private ArrayList<Integer> rowSpannersConfidenceColumnsSpanned;
    private ArrayList<Double> rowSpannersConfidenceLineDistance;

    /**
     * This is the constructor of the SemanticFramework class.
     * It sets the local variables from the parameters and calls the methods for calculating the semantic parts.
     * @param table A list containing the column objects of the table.
     * @param verticalHeightThreshold The threshold used for checking if two lines have a whitespace in between them
     * @param rowSpanners The potential subheaders/rowspanners, these need to be validated
     * @param horizontalLengthThreshold The threshold for calculating the distance between two cells
     * @param rawTable A lis containing the table in Line objects.
     * @param validation The validation object as calculated in the table class.
     * @param title The potential title of the table.
     */
    public SemanticFramework(ArrayList<Column2> table, double verticalHeightThreshold, ArrayList<Line> rowSpanners, double horizontalLengthThreshold, ArrayList<Line> rawTable, Validation validation, ArrayList<Line> title){
        LOGGER.info("Starting table semantics");
        this.table = table;
        this.rawTable = rawTable;
        this.validatedRowSpanners = new ArrayList<Line>();
        this.rowSpannersConfidenceAlignment = new ArrayList<Double>();
        this.rowSpannersConfidenceColumnsSpanned = new ArrayList<Integer>();
        this.rowSpannersConfidenceLineDistance = new ArrayList<Double>();

        this.verticalHeightThreshold = verticalHeightThreshold;
        this.horizontalLengthThreshold = horizontalLengthThreshold;
        this.rowSpanners = rowSpanners;
        this.identifiersConfidenceAlignment = new ArrayList<Double>();
        this.identifiersConfidenceColumnsSpanned = new ArrayList<Integer>();
        this.title = title;
        this.titleConfidence = 1;
        this.headers = new ArrayList<Line>();
        if(title.size() ==0){
            findTitle();
        }
        checkTheTitle();
        System.out.println("Title: " + title);
        validateIdentifiersOnLineDistance(validation);
        findMostFrequentAlignment();
        validateRowSpaners();
        identifierSpansMultipleColumns();

        findHeaders();
        System.out.println("Headers: " + headers);
        distinguishSubHeadersFromRowSpanners();
        if(title.isEmpty()){
            titleConfidence = 0;
        }
    }

    /**
     * This method finds the most occuring alignment of a column. This will be used as a base for the column in calculating
     * if a single partition line is aligned with a column or not. The base is not exact as we want to take cells that differ
     * by just a couple of pixels to include the base as well.
     */
    private void findMostFrequentAlignment(){
        ArrayList<Double> listOfBaseX1 = new ArrayList<Double>();
        for(Column2 column : table){
            HashMap<Integer, Integer> boundaryMap = column.getBoundaryMap();
            String stringStep = Collections.max(boundaryMap.values()).toString();  // This will return max value in the Hashmap
            ArrayList<Integer> baseX1 = new ArrayList<Integer>();
            int maxValue = Integer.parseInt(stringStep);
            int keyOfMaxValue = 0;
            for(int key : boundaryMap.keySet()){
                if(boundaryMap.get(key).equals(maxValue)){
                    keyOfMaxValue = key;
                }
            }
            for(int key : boundaryMap.keySet()){
                if(!(boundaryMap.get(key) == maxValue)){
                    if(CommonMethods.calcDistance(key, keyOfMaxValue)<(horizontalLengthThreshold/10)){
                        baseX1.add(key);
                    }
                }
            }
            baseX1.add(keyOfMaxValue);
            listOfBaseX1.add(CommonMethods.average(baseX1));
        }
        this.listOfBaseX1 = listOfBaseX1;
    }

    /**
     * This method validates rowspanners/subheaders by looking at the distance between the base of the column the cell
     * touches/aligns with and checking the distance from this base in pixels.
     */
    private void validateRowSpaners(){
        for(Line line : rowSpanners){
            int x1OfRowspan = line.getCellObject().getX1();
            int x2OfRowspan = line.getCellObject().getX2();
            for(double baseX1 :  listOfBaseX1){
                if(CommonMethods.calcDistance(x1OfRowspan, baseX1)>(horizontalLengthThreshold/10)&&(
                        table.get(listOfBaseX1.indexOf(baseX1)).touchesColumn(x1OfRowspan, x2OfRowspan))||
                        table.get(listOfBaseX1.indexOf(baseX1)).fitsInColumn(x1OfRowspan, x2OfRowspan)){
                    if(x1OfRowspan > baseX1){
                        this.identifiersConfidenceAlignment.add((-CommonMethods.calcDistance(x1OfRowspan, baseX1)) / (horizontalLengthThreshold / 10));
                    }
                    else{
                        this.identifiersConfidenceAlignment.add(CommonMethods.calcDistance(x1OfRowspan, baseX1)/(horizontalLengthThreshold/10));
                    }
                    break;
                }
                if(listOfBaseX1.indexOf(baseX1) == listOfBaseX1.size()-1){
                    this.identifiersConfidenceAlignment.add(-100000.0);
                }
            }
        }
    }

    /**
     * This method checks if the identifiers spans multiple columns. This can be an important aspect for a line being a subheader
     * or a rowspan.
     */
    private void identifierSpansMultipleColumns(){
        ArrayList<Line> wrongRowSpanners = new ArrayList<Line>();
        for(Line line : rowSpanners){
            int x1 = line.getCellObject().getX1();
            int x2 = line.getCellObject().getX2();
            int columnSpanned = 0;
            for(Column2 column : table){
                if(column.columnFitsIn(x1, x2)||column.touchesColumn(x1,x2)||column.fitsInColumn(x1,x2)||column.spansColumn(x1,x2)){
                    columnSpanned+=1;
                }
            }
            if(columnSpanned==table.size()&&(line.getAverageY1()>rawTable.get(0).getAverageY2()
                    ||line.getAverageY1()>rawTable.get(1).getAverageY2())){
                this.titleConfidence = titleConfidence - 0.1;
                title.add(line);
                wrongRowSpanners.add(line);
            }
            else{
                this.identifiersConfidenceColumnsSpanned.add(columnSpanned);
            }
        }
        for(Line line: wrongRowSpanners){
            rowSpanners.remove(line);
        }
    }

    /**
     * If there is a small amount of whitespace between a two lines that this might be an indication that this line is
     * a subheader. This method/validation checks the potential subheaders for this aspect.
     * @param validation The validation object as created in the Table class.
     */
    private void validateIdentifiersOnLineDistance(Validation validation){
        double lastY2 = 0.0;
        ArrayList<Double> identifiersConfidenceLineDistance = new ArrayList<Double>();
        for(Line rowSpanningLine : rowSpanners){
            double y1RowSpanner = rowSpanningLine.getAverageY1();
            for(Line tableLine : rawTable){
                double currentY2 = tableLine.getAverageY2();
                if(y1RowSpanner<currentY2){     //then this is the line where the rowSpanner should fit.
                    if(rawTable.indexOf(tableLine)==0){           //to check if this is the first row.
                        try{
                            identifiersConfidenceLineDistance.add(validation.getTitleConfidence().get(0));
                            break;
                        }
                        catch (IndexOutOfBoundsException e){
                            identifiersConfidenceLineDistance.add(1.0);
                        }
                    }
                    else{
                        double distance = lastY2 - y1RowSpanner;
                        identifiersConfidenceLineDistance.add((-distance)/(verticalHeightThreshold));
                        break;
                    }
                }
                lastY2 = currentY2;
            }
        }
        this.identifiersConfidenceLineDistance = identifiersConfidenceLineDistance;
    }

    /**
     * This method finds the title and removes it from the rawTable.
     * This method is only called for cases where the clustersize is not sufficient and might contain some obscure rules.
     */
    private void findTitle(){
        if(rawTable.get(0).getClusterSize() > 1){
            this.title.add(rawTable.get(0));
            this.titleConfidence = 0.3;
            rawTable.remove(0);
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
        System.out.println(title);
        System.out.println("T-Size: " + title.size());
        if((title.size() >= 2)){
            Line lastCellInTitle = title.get(title.size()-1);
            double distanceBetweenTitle = lastCellInTitle.getAverageY1()- title.get(title.size()-2).getAverageY2();
            double distanceBetweenTable = rawTable.get(0).getAverageY1()- lastCellInTitle.getAverageY2();
            int minY1Column = Integer.MIN_VALUE;
            for(Column2 column : table){
                if(column.getMinY1() > minY1Column){
                    minY1Column = column.getMinY1();
                }
            }
            System.out.println(distanceBetweenTable+" VS " + distanceBetweenTitle);
            if(distanceBetweenTable < distanceBetweenTitle){
                rowSpanners.add(0, title.get(title.size()-1));
                title.remove(title.size()-1);
            }
        }
    }


    //TODO: Make the 5 threshold a parameter called allowedHeaderSize
    //TODO: Make allowed header iterations a parameter.
    /**
     * This method will try to find the headers in the table. It does so by assuming that every line in the data is a header
     * until the vertical height threshold is smaller then the distance between the current line and the next line.
     * Validation of this method is also based on the number of lines in the headers.
     */
    private void findHeaders(){
        int loopConfidence = 0;
        int loops = 0;
        boolean iterating = true;
        while (iterating){
            ArrayList<Line> headers = new ArrayList<Line>();
            ArrayList<Double> headerConfidence = new ArrayList<Double>();
            double lastY2 = 0.0;
            boolean breaking = false;
//            System.out.println("Them threshold: " + verticalHeightThreshold);
            int maxHeaderSize = 3;
            for(Line line : rawTable){
                if(loops > 3){
                    headers = new ArrayList<Line>();
                    this.headers = headers;
                    this.headerConfidence = new ArrayList<Double>();
                    this.headerConfidence.add(0.0);
                    iterating = false;
                    break;
                }
                if(rawTable.indexOf(line) >= rawTable.size()/2){
                    break;
                }
                double currentY2 = line.getAverageY2();
                double currentY1 = line.getAverageY1();
                double distance = currentY1 - lastY2;
//                System.out.println(line);
//                System.out.println(distance + " " + verticalHeightThreshold);
//                System.out.println(headers);
                if(distance > verticalHeightThreshold&&breaking){                                    //lastY2 != 0.0 &&
                    if(lastY2 != 0.0 ){
                        headers.add(rawTable.get(0));
                    }
                    headerConfidence.add(distance/(verticalHeightThreshold/headers.size())+loopConfidence);
                    break;
                }
                else if(distance > verticalHeightThreshold){                                        //lastY2 != 0.0 &&
                    breaking = true;
                }                                                                                        //TODO Check the code below and test.
/*                else if(breaking){
                    headers.add(line);
                    maxHeaderSize +=1;                         //Add the rowSpanner to the headers
                    breaking = false;
                }*/
                else if(!rowSpanners.isEmpty() && rowSpanners.get(0).getAverageY1() > currentY1 && !rowSpanners.contains(line)){
                    headers.add(line);
                }
                else if(rowSpanners.isEmpty()){
                    headers.add(line);
                }
                lastY2 = currentY2;
            }
            if(headers.size() > maxHeaderSize || headers.size() < 1){
                loopConfidence = -1;
                this.verticalHeightThreshold = verticalHeightThreshold/1.5;
                loops+=1;
                continue;
            }

            int highestClusterSize = 0;
            for(Line line : headers){
                if(highestClusterSize < line.getClusterSize()){
                    highestClusterSize = line.getClusterSize();
                }
            }

            if(highestClusterSize == table.size()){                     //if the highest amount of partitions in the header == the amount of columns found you get bonus confidence!
                headerConfidence.set(0, headerConfidence.get(0) + 0.5);
            }

            System.out.println("headers:" + headers);
            this.headers.addAll(headers);
            if(!headerConfidence.isEmpty()){
                this.headerConfidence = headerConfidence;
            }
            else{
                headerConfidence.add((double)-headers.size());
                this.headerConfidence = headerConfidence;
            }
            break;
        }
    }

    /**
     * This method uses the validation scores to determine if single partition lines are rowspanners or subheaders.
     * It does this by looking at the three parameters found earlier.
     */
    private void distinguishSubHeadersFromRowSpanners(){
        ArrayList<Integer> indexToBeRemoved = new ArrayList<Integer>();
        System.out.println(rowSpanners);
        System.out.println(identifiersConfidenceLineDistance);
        System.out.println(identifiersConfidenceColumnsSpanned);
        System.out.println(identifiersConfidenceAlignment);
        System.out.println(rowSpannersConfidenceAlignment);
        System.out.println(rowSpannersConfidenceColumnsSpanned);
        System.out.println(rowSpannersConfidenceLineDistance);
        for(int x = 0; x<rowSpanners.size();x++){
            if(rowSpanners.size() <= x){
                identifiersConfidenceAlignment.add(-10.0);
                identifiersConfidenceColumnsSpanned.add(-10);
                identifiersConfidenceLineDistance.add(-10.0);
                break;
            }
            else if(identifiersConfidenceAlignment.get(x) <=-500){
                headers.add(0,rowSpanners.get(x));
                indexToBeRemoved.add(x);
            }
            else if(identifiersConfidenceAlignment.get(x)<=0
                    &&identifiersConfidenceColumnsSpanned.get(x)<3
                    &&identifiersConfidenceLineDistance.get(x)<1.5
                    &&rowSpanners.get(x).getAverageY1()>headers.get(headers.size()-1).getAverageY2()){
                System.out.println("Identifier! " + rowSpanners.get(x));
                indexToBeRemoved.add(x);
            }
        }
        int index = 0;
        for(int x : indexToBeRemoved){
            if(identifiersConfidenceAlignment.get(x-index) > -500){
                this.validatedRowSpanners.add(rowSpanners.get(x - index));
                this.rowSpannersConfidenceAlignment.add(identifiersConfidenceAlignment.get(x - index));
                this.rowSpannersConfidenceColumnsSpanned.add(identifiersConfidenceColumnsSpanned.get(x-index));
                this.rowSpannersConfidenceLineDistance.add(identifiersConfidenceLineDistance.get(x - index));

            }
            rowSpanners.remove(x-index);
            identifiersConfidenceAlignment.remove(x-index);
            identifiersConfidenceColumnsSpanned.remove(x - index);
            identifiersConfidenceLineDistance.get(x - index);
            index +=1;
        }
    }

    /**
     * This method returns the semantic parts that have been calculated in valid XML format. This can be used in the output
     * of the program.
     * @return A string containing the content of the class in valid XML.
     */
    public String getXML(){
        String content = "";
        content = content + "    <tableSemantics>\n";
        content = content + "        <title>"+CommonMethods.changeIllegalXMLCharacters(title.toString())+"</title>\n";
        content = content + "        <titleConfidence>" +titleConfidence+"</titleConfidence>\n";
        if(!rowSpanners.isEmpty()){
            content = content + "        <subHeaders>" + CommonMethods.changeIllegalXMLCharacters(rowSpanners.toString()) + "</subHeaders>\n";
            content = content + "        <subHeadersConfidenceAlignment>" + identifiersConfidenceAlignment +"</subHeadersConfidenceAlignment>\n";
            content = content + "        <subHeadersConfidenceColumnsSpanned>" + identifiersConfidenceColumnsSpanned + "</subHeadersConfidenceColumnsSpanned>\n";
            content = content + "        <subHeadersConfidenceLineDistance>" + identifiersConfidenceLineDistance + "</subHeadersConfidenceLineDistance>\n";
        }
        if(!(validatedRowSpanners.isEmpty())){
            content = content + "        <rowSpanners>" + CommonMethods.changeIllegalXMLCharacters(validatedRowSpanners.toString())+"</rowSpanners>\n";
            content = content + "        <rowSpannersConfidenceAlignment>" + CommonMethods.makeListPositive(rowSpannersConfidenceAlignment) + "</rowSpannersConfidenceAlignment>\n";
            content = content + "        <rowSpannersConfidenceColumnsSpanned>" + rowSpannersConfidenceColumnsSpanned + "</rowSpannersConfidenceColumnsSpanned>\n";
            content = content + "        <rowSpannersConfidenceLineDistance>" + CommonMethods.addNumberToList(rowSpannersConfidenceLineDistance, 1.5) + "</rowSpannersConfidenceLineDistance>\n";
        }
        content = content + "        <headers>" + CommonMethods.changeIllegalXMLCharacters(headers.toString()) + "</headers>\n";
        content = content + "        <headerConfidence>" + headerConfidence + "</headerConfidence>\n";
        content = content + "    </tableSemantics>\n";
        return content;
    }

    /**
     * The toString method of the SemanticFramework class. It gives an overview of all the semantic table parts and their confidence.
     * This method also logs to the given log file.
     * If certain parts are not available (for example, because a table has no rowspans) then the method will not print or log them.
     * @return The calculated table parts and their confidence.
     */
    public String toString(){
        String content = "";
        content = content + "Title of the table: "+ title + "\n";
        content = content + "Title Confidence: " + titleConfidence + "\n";
        if(!rowSpanners.isEmpty()){
            content = content + "Subheaders: " + rowSpanners + "\n";
            content = content + "Subheaders confidence based on alignment: " + identifiersConfidenceAlignment + "\n";
            content = content + "Subheaders confidence number of columns spanned: " + identifiersConfidenceColumnsSpanned + "\n";
            content = content + "Subheaders confidence distance between the line above: " + identifiersConfidenceLineDistance + "\n";
        }
        if(!validatedRowSpanners.isEmpty()){
            content = content + "Rowspanners: " + validatedRowSpanners+"\n";
            content = content + "Rowspanners confidence based on alignment" + rowSpannersConfidenceAlignment + "\n";
            content = content + "Rowspanners confidence based on the number of columns spanned:" + rowSpannersConfidenceColumnsSpanned + "\n";
            content = content + "Rowspanners confidence based on the distance with the line above: " + rowSpannersConfidenceLineDistance + "\n";
        }
        content = content + "Headers: " + headers + "\n";
        content = content + "Header confidence: " + headerConfidence + "\n";
        LOGGER.info(content);
        return content;
    }
}
