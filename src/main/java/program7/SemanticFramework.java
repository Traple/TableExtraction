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
    private double verticalHeightThreshold;
    private ArrayList<Line> rowSpanners;
    private double horizontalLengthThreshold;
    private ArrayList<Double> listOfBaseX1;

    public SemanticFramework(ArrayList<Column2> table, double verticalHeightThreshold, ArrayList<Line> rowSpanners, double horizontalLengthThreshold){
        System.out.println("Starting table semantics:");
        this.table = table;
        this.verticalHeightThreshold = verticalHeightThreshold;
        this.horizontalLengthThreshold = horizontalLengthThreshold;
        this.rowSpanners = rowSpanners;         //Are note validated yet.
        findMostFrequentAlignment();

        validateRowSpaners();

//We dont find the headers for now, first we need the rowspans/identifiers
//        findHeaders();
    }

    //TODO: Warning: The following is a bit creative......
    private void findMostFrequentAlignment(){
        System.out.println(horizontalLengthThreshold);
        ArrayList<Double> listOfBaseX1 = new ArrayList<Double>();

        for(Column2 column : table){
            HashMap<Integer, Integer> boundaryMap = column.getBoundaryMap();
            String stringStep = Collections.max(boundaryMap.values()).toString();  // This will return max value in the Hashmap

            ArrayList<Integer> baseX1 = new ArrayList<Integer>();
            int maxValue = Integer.parseInt(stringStep);
            int maxValues=0;
            int keyOfMaxValue = 0;
            for(int key : boundaryMap.keySet()){
                if(boundaryMap.get(key).equals(maxValue)){
                    keyOfMaxValue = key;
                }
            }
            for(int key : boundaryMap.keySet()){
                if(maxValues >1){
                    System.out.println("error!");
                }
                if(!(boundaryMap.get(key) == maxValue)){
                    if(CommonMethods.calcDistance(key, keyOfMaxValue)<(horizontalLengthThreshold/10)){
                        System.out.println("belongs: " +key + " with: "+keyOfMaxValue+"??");
                        baseX1.add(key);
                    }
                }
                else{
                    maxValues++;
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
            for(double baseX1 :  listOfBaseX1){
                if(CommonMethods.calcDistance(x1OfRowspan, baseX1)>(horizontalLengthThreshold/10)){
                    System.out.println("je suis aligned? Hon Hon Hon!" + line.getCellObject());
                    break;
                }
            }
        }
    }

    /**
     * This method will try to find the headers based on the vertical positions.
     */
    private void findHeaders(){
        System.out.println(table);
        for(Column2 column : table){
            ArrayList<Cell> cells = column.getCellObjects();
            int previousY2 = cells.get(0).getY2();
            for(Cell cell : cells.subList(1,cells.size()-1)){
                int y1 = cell.getY1();
                int y2 = cell.getY2();
                int distance = y1 - previousY2;
                System.out.println(distance);
                if(distance > verticalHeightThreshold){

                }

                previousY2 = y2;
            }
        }
    }


}
