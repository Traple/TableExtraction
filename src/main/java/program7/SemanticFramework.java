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

    public SemanticFramework(ArrayList<Column2> table, double verticalHeightThreshold, ArrayList<Line> rowSpanners, double horizontalLengthThreshold){
        System.out.println("Starting table semantics:");
        this.table = table;
        this.verticalHeightThreshold = verticalHeightThreshold;
        this.horizontalLengthThreshold = horizontalLengthThreshold;
        this.rowSpanners = rowSpanners;         //Are note validated yet.
//        findMostFrequentAlignment();

//        validateRowSpaners();

//We dont find the headers for now, first we need the rowspans/identifiers
//        findHeaders();
    }
    private void findMostFrequentAlignment(){
        System.out.println(horizontalLengthThreshold);
        for(Column2 column : table){
            HashMap<Integer, Integer> boundaryMap = column.getBoundaryMap();

            String stringStep = Collections.max(boundaryMap.values()).toString();  // This will return max value in the Hashmap
            int maxValue = Integer.parseInt(stringStep);
            int maxValues=0;
            System.out.println(maxValue);
            for(int key : boundaryMap.keySet()){
                if(maxValues >1){
                    System.out.println("error!");
                    System.out.println(boundaryMap);
                }
                if(!(boundaryMap.get(key) == maxValue)){
                    if(CommonMethods.calcDistance(boundaryMap.get(key), maxValue)<(horizontalLengthThreshold/10)){

                    }
                    else{
                        System.out.println("Aligned????" + key + " "+boundaryMap.get(key) +"|||||"+ column);
                    }
                }
                else{
                    maxValues++;
                }
            }
        }
    }

    private void validateRowSpaners(){
        for(Line line : rowSpanners){
            System.out.println(line.toString());
            System.out.println(line.getCellObject().getX1());
            int x1OfRowspan = line.getCellObject().getX1();
            for(Column2 column :  table){
                if(x1OfRowspan == column.getColumnBoundaryX1()){

                }
                System.out.println(column);
            }
//            System.out.println();
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
