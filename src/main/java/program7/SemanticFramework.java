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
    private ArrayList<Line> headers;
    private ArrayList<Double> headerConfidence;
    private double titleConfidence;
    private ArrayList<Line> validatedRowSpanners;
    private ArrayList<Double> rowSpannersConfidenceAlignment;
    private ArrayList<Integer> rowSpannersConfidenceColumnsSpanned;
    private ArrayList<Double> rowSpannersConfidenceLineDistance;

    public SemanticFramework(ArrayList<Column2> table, double verticalHeightThreshold, ArrayList<Line> rowSpanners, double horizontalLengthThreshold, ArrayList<Line> rawTable, Validation validation, ArrayList<Line> title){
        System.out.println("Starting table semantics:");
        this.table = table;
        this.rawTable = rawTable;
        this.validatedRowSpanners = new ArrayList<Line>();
        this.rowSpannersConfidenceAlignment = new ArrayList<Double>();
        this.rowSpannersConfidenceColumnsSpanned = new ArrayList<Integer>();
        this.rowSpannersConfidenceLineDistance = new ArrayList<Double>();

        this.verticalHeightThreshold = verticalHeightThreshold;
        this.horizontalLengthThreshold = horizontalLengthThreshold;
        this.rowSpanners = rowSpanners;
        this.identifiersFromAlignment = new ArrayList<String>();
        this.identifiersConfidenceAlignment = new ArrayList<Double>();
        this.identifiersFromColumnSpans = new ArrayList<String>();
        this.identifiersConfidenceColumnsSpanned = new ArrayList<Integer>();
        this.title = title;
        this.titleConfidence = 1;
        findMostFrequentAlignment();
        validateRowSpaners();
        identifierSpansMultipleColumns();
        validateIdentifiersOnLineDistance(validation);
        findHeaders();
        distinguishSubHeadersFromRowSpanners();
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
                if(CommonMethods.calcDistance(x1OfRowspan, baseX1)>(horizontalLengthThreshold/10)&&(
                        table.get(listOfBaseX1.indexOf(baseX1)).touchesColumn(x1OfRowspan, x2OfRowspan))||
                        table.get(listOfBaseX1.indexOf(baseX1)).fitsInColumn(x1OfRowspan, x2OfRowspan)){
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
                this.identifiersFromColumnSpans.add(line.toString());
                this.identifiersConfidenceColumnsSpanned.add(columnSpanned);
            }
        }
        for(Line line: wrongRowSpanners){
            rowSpanners.remove(line);
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

    private void findHeaders(){
        ArrayList<Line> headers = new ArrayList<Line>();
        ArrayList<Double> headerConfidence = new ArrayList<Double>();
        double lastY2 = 0.0;
        for(Line line : rawTable){
            double currentY2 = line.getAverageY2();
            double currentY1 = line.getAverageY1();
            double distance = currentY1 - lastY2;
            System.out.println(line);
            System.out.println(distance+ " vs " + verticalHeightThreshold );
            System.out.println(headers.size());
            if(lastY2 != 0.0 && distance > verticalHeightThreshold/headers.size()){
                headerConfidence.add(distance/(verticalHeightThreshold/headers.size()));
                break;
            }
            else{
                headers.add(line);
            }
            lastY2 = currentY2;
        }
        System.out.println("headers:" + headers);
        this.headers = headers;
        this.headerConfidence = headerConfidence;
    }

    //TODO: Fix the distinguish method as adding and removing files gives errors or mistakes.
    private void distinguishSubHeadersFromRowSpanners(){
        ArrayList<Integer> indexToBeRemoved = new ArrayList<Integer>();
        for(int x = 0; x<rowSpanners.size();x++){
            if(identifiersConfidenceAlignment.get(x)<0
                    &&identifiersConfidenceColumnsSpanned.get(x)<3
                    &&identifiersConfidenceLineDistance.get(x)<1.5
                    &&rowSpanners.get(x).getAverageY1()>headers.get(headers.size()-1).getAverageY2()){
                System.out.println("Identifier! " + rowSpanners.get(x));
                indexToBeRemoved.add(x);
            }
        }
        int index = 0;
        for(int x : indexToBeRemoved){
            this.validatedRowSpanners.add(rowSpanners.get(x-index));
            this.rowSpannersConfidenceAlignment.add(identifiersConfidenceAlignment.get(x-index));
            this.rowSpannersConfidenceColumnsSpanned.add(identifiersConfidenceColumnsSpanned.get(x-index));
            this.rowSpannersConfidenceLineDistance.add(identifiersConfidenceLineDistance.get(x-index));
            rowSpanners.remove(x-index);
            identifiersConfidenceAlignment.remove(x-index);
            identifiersFromColumnSpans.remove(x-index);
            identifiersConfidenceLineDistance.get(x-index);
            index +=1;
        }
    }
    //TODO: Distinguish between subheaders and rowspanners.

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
}
