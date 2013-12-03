package program7;

import java.util.*;

/**
 * This class will provide a semantic framework for the table. This consists of the following points:
 * - Title
 * - Headers
 * - Data
 * - Possible Rowspans/Identifiers
 * - Possible Columnspans
 *
 * Steps:
 * - Find identation (to the left or to the right?)
 * - Link identation with rowspans
 * - Find distance between lines and link it to rowspans
 * - Find if rowspans span multiple columns and if they start at the first column
 * - Find headers
 * - Check added rowspans if their not a header.
 *
 * Every point should contain their expected value and a validation score
 */
public class SemanticFramework {

    private ArrayList<Column2> table = new ArrayList<Column2>();
    private ArrayList<Line> rawTable;
    private double verticalHeightThreshold;
    private ArrayList<Line> rowSpanners;
    private double horizontalLengthThreshold;
    private ArrayList<Double> listOfBaseX1;
    private ArrayList<String> identifiersFromAlignment;
    private ArrayList<Double> identifiersConfidenceAlignment;
    private ArrayList<String> identifiersFromColumnSpans;
    private ArrayList<Integer> identifiersConfidenceColumnsSpanned;
    private ArrayList<Double> identifiersConfidenceLineDistance;
    private ArrayList<Line> title;

    public SemanticFramework(ArrayList<Column2> table, double verticalHeightThreshold, ArrayList<Line> rowSpanners, double horizontalLengthThreshold, ArrayList<Line> rawTable, Validation validation, ArrayList<Line> title){
        System.out.println("Starting table semantics:");
        this.table = table;
        this.rawTable = rawTable;
        this.verticalHeightThreshold = verticalHeightThreshold;
        this.horizontalLengthThreshold = horizontalLengthThreshold;
        this.rowSpanners = rowSpanners;
        this.identifiersFromAlignment = new ArrayList<String>();
        this.identifiersConfidenceAlignment = new ArrayList<Double>();
        this.identifiersFromColumnSpans = new ArrayList<String>();
        this.identifiersConfidenceColumnsSpanned = new ArrayList<Integer>();
        this.title = title;
        findMostFrequentAlignment();
        validateRowSpaners();
        identifierSpansMultipleColumns();
        validateIdentifiersOnLineDistance(validation);
        findHeaders();
    }
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
    private void validateRowSpaners(){
        for(Line line : rowSpanners){
            int x1OfRowspan = line.getCellObject().getX1();
            int x2OfRowspan = line.getCellObject().getX2();
            for(double baseX1 :  listOfBaseX1){
                if(CommonMethods.calcDistance(x1OfRowspan, baseX1)>(horizontalLengthThreshold/10)&&
                        table.get(listOfBaseX1.indexOf(baseX1)).touchesColumn(x1OfRowspan, x2OfRowspan)){
                    if(x1OfRowspan > baseX1){
                        this.identifiersFromAlignment.add(line.toString());
                        this.identifiersConfidenceAlignment.add((-CommonMethods.calcDistance(x1OfRowspan, baseX1)) / (horizontalLengthThreshold / 10));
                    }
                    else{
                        this.identifiersFromAlignment.add(line.toString());
                        this.identifiersConfidenceAlignment.add(CommonMethods.calcDistance(x1OfRowspan, baseX1)/(horizontalLengthThreshold/10));
                    }
                    break;
                }
            }
        }
    }
    private void identifierSpansMultipleColumns(){
        int columnSpanned = 0;
        for(Line line : rowSpanners){
            int x1 = line.getCellObject().getX1();
            int x2 = line.getCellObject().getX2();

            for(Column2 column : table){
                if(column.columnFitsIn(x1, x2)||column.touchesColumn(x1,x2)||column.fitsInColumn(x1,x2)||column.spansColumn(x1,x2)){
                    columnSpanned+=1;
                }
            }
            this.identifiersFromColumnSpans.add(line.toString());
            this.identifiersConfidenceColumnsSpanned.add(columnSpanned);
            columnSpanned = 0;
        }
    }
    private void validateIdentifiersOnLineDistance(Validation validation){
        double lastY2 = 0.0;
        ArrayList<Double> identifiersConfidenceLineDistance = new ArrayList<Double>();
        for(Line rowSpanningLine : rowSpanners){
            double y1RowSpanner = rowSpanningLine.getAverageY1();
            for(Line tableLine : rawTable){
                double currentY2 = tableLine.getAverageY2();
                if(y1RowSpanner<currentY2){     //then this is the line where the rowSpanner should fit.
                    if(rawTable.indexOf(tableLine)==0){           //to check if this is the first row.
                        identifiersConfidenceLineDistance.add(validation.getTitleConfidence().get(0));
                        break;
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

    private void findHeaders(){
        System.out.println("Stop. Header time:");
        System.out.println(title);
        for(Line line : rawTable){
            System.out.println(line);
        }
    }

    public String getXML(){
        String content = "";
        content = content + "    <tableSemantics>\n";
        content = content + "    <title>"+CommonMethods.changeIllegalXMLCharacters(title.toString())+"</title>\n";
        content = content + "        <identifiers>" + CommonMethods.changeIllegalXMLCharacters(identifiersFromAlignment.toString()) + "</identifiers>\n";
        content = content + "        <identifiersConfidenceAlignment>" + identifiersConfidenceAlignment +"</identifiersConfidenceAlignment>\n";
        content = content + "        <identifiersConfidenceColumnsSpanned>" + identifiersConfidenceColumnsSpanned + "</identifiersConfidenceColumnsSpanned>\n";
        content = content + "        <identifiersConfidenceLineDistance>" + identifiersConfidenceLineDistance + "</identifiersConfidenceLineDistance>\n";
        content = content + "    </tableSemantics>\n";
        return content;
    }
}
