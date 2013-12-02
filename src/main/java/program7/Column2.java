package program7;

import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The column class contains methods that can be used to find headers and gain information on a column basis.
 * For example when looking for headers we might want to take the space between rows into account. the column class contains
 * a method to check if this is viable by returning an internal score.
 */
public class Column2 {
    private ArrayList<ArrayList<Element>> cells;
    private int columnBoundaryX1;
    private int columnBoundaryX2;
    private double AVGCharDistance;
    private HashMap<Integer, Integer> boundaryMap;
    private int mostFrequentX1;

    /**
     * This is the constructor of the Column class. It will set up two private variables and after that calculate the
     * left and right boundaries of the column using the findColumnBoundaries() method.
     * @param cells This is a list containing the the partitions of the words in the column.
     * @param AVGCharDistance The average distance of a character as calculated in the page class.
     */
    public Column2(ArrayList<ArrayList<Element>> cells, double AVGCharDistance){
        this.cells = cells;
        this.AVGCharDistance = AVGCharDistance;

        findColumnBoundaries();
        findMostFrequentX1Boundary();
    }

    /**
     * This method will try to find the X1 and X2 of the column.
     * It does this by looping trough the partitions and check if the X1 and X2 are currently the highest or lowest values.
     * It will set this value in the private columnBoundaryX1 and columnBoundaryX2 variables.
     */
    private void findColumnBoundaries(){
        int columnBoundaryX1 = Integer.MAX_VALUE;
        int columnBoundaryX2 = Integer.MIN_VALUE;
        for(ArrayList<Element> cell : cells){
            String pos;
            String[] positions;

            Element firstWordInCell = cell.get(0);
            Element lastWordInCell = cell.get(cell.size()-1);

            pos = firstWordInCell.attr("title");
            positions = pos.split("\\s+");
            int x1 = Integer.parseInt(positions[1]);
            if(x1 < columnBoundaryX1){
                columnBoundaryX1 = x1;
                this.columnBoundaryX1 = x1;
            }

            pos = lastWordInCell.attr("title");
            positions = pos.split("\\s+");
            int x2 = Integer.parseInt(positions[3]);
            if(x2 > columnBoundaryX2){
                columnBoundaryX2 = x2;
                this.columnBoundaryX2 = x2;
            }
        }
    }

    private void findMostFrequentX1Boundary(){
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        for(ArrayList<Element> cell : cells){
            String pos;
            String[] positions;
            Element firstWordInCell = cell.get(0);

            pos = firstWordInCell.attr("title");
            positions = pos.split("\\s+");
            int x1 = Integer.parseInt(positions[1]);

            if(map.containsKey(x1)){
                map.put(x1, (map.get(x1).intValue()+1));
            }
            else{
                map.put(x1, 1);
            }
        }
        this.boundaryMap = map;
    }

    /**
     * This method can be used outside the class to add a new cell in the Column.
     * The method will check the Y2 of the new partition that one wants to add and see where it fits inside the current column.
     * After that the method checks the column boundaries using the findColumnBoundaries() method.
     * @param newCell This is a new partition that can be added to the column. It is a list of words picked up by the OCR.
     */
    public void addCell(ArrayList<Element> newCell){
        ArrayList<ArrayList<Element>> newCells = new ArrayList<ArrayList<Element>>();
        String pos;
        String[] positions;
        pos = newCell.get(0).attr("title");
        positions = pos.split("\\s+");
        int newCellY2 = Integer.parseInt(positions[4]);
        boolean notAdded = true;
        int y1;
        for(ArrayList<Element> cell : cells){
            pos = cell.get(0).attr("title");
            positions = pos.split("\\s+");
            y1 = Integer.parseInt(positions[2]);
            if((newCellY2<y1&&notAdded)){
                newCells.add(newCell);
                newCells.add(cell);
                notAdded = false;
            }
            else if(cells.get(cells.size()-1).equals(cell)&&notAdded){
                newCells.add(cell);
                newCells.add(newCell);
            }
            else {
                newCells.add(cell);
            }
        }
        this.cells = newCells;
        findColumnBoundaries();
    }
    //~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~
    //Getters:

    /**
     * This method will find the lowest Y value (i.o.w. the start of the column).
     * @return The lowest Y1 value (the top pixel of the column).
     */
    public int getMinY1(){
        String[] positions;
        int y1;
        int minY1 = Integer.MAX_VALUE;
        for(ArrayList<Element> cell : cells){
            String pos = cell.get(0).attr("title");
            positions = pos.split("\\s+");
            y1 = Integer.parseInt(positions[2]);
            if(minY1 > y1){
                minY1 = y1;
            }
        }
        return minY1;
    }

    /**
     * This method checks if two positions (X1 and X2) fit inside the column boundaries of the column.
     * The method uses the average distance of a character to compensate for cases where the partition would almost fit.
     * @param x1 The left position
     * @param x2 The right position
     * @return A boolean. The method returns true if the two values fit inside the column boundaries. The method also returns
     * True if both or one of the values equals the column boundaries. In all other cases the method returns false.
     */
    public boolean fitsInColumn(int x1, int x2){
        return x1 >= columnBoundaryX1-(AVGCharDistance/2)&&x2<=columnBoundaryX2+(AVGCharDistance/2);
    }
    /**
     * This method checks if the column boundaries fit inside two given coordinates.
     * The method uses the average distance of a character to compensate for cases where the column would almost fit.
     * @param x1 The left position
     * @param x2 The right position
     * @return A boolean. The method returns true if the column boundaries fit inside the two given values. The method also returns
     * True if both or one of the values equals the column boundaries. In all other cases the method returns false.
     */
    public boolean columnFitsIn(int x1, int x2){
        return x1 <= columnBoundaryX1+(AVGCharDistance/2)&&x2>=columnBoundaryX2-(AVGCharDistance/2);
    }
    /**
     * This method checks if one of the two given X positions is inside the column boundaries.
     * outside the column boundaries.
     * @param x1 the left position
     * @param x2 the right position
     * @return A boolean. The method returns true if one of the two given values is inside the column boundaries and the
     * other is outside the column boundaries. If one the two values equals the column boundaries the method returns true as well.
     * In all other cases the method returns false.
     * In this method the average distance of a character is not used to compensate for cases where the partition "almost touches" the column.
     */
    public boolean touchesColumn(int x1, int x2){
        return (x1 < columnBoundaryX1 && x2 >columnBoundaryX1) || (x1 > columnBoundaryX1 && x2 < columnBoundaryX2);
    }

    public ArrayList<Cell> getCellObjects(){
        ArrayList<Cell> cellObjects = new ArrayList<Cell>();
        for(ArrayList<Element> cell : cells){
            Cell currentCell = new Cell(cell);
            cellObjects.add(currentCell);
        }
        return cellObjects;
    }
    public HashMap<Integer, Integer> getBoundaryMap(){
        return boundaryMap;
    }

    @SuppressWarnings("UnusedDeclaration")              //We want to offer these methods for future use.
    public int getColumnBoundaryX1(){
        return columnBoundaryX1;
    }
    @SuppressWarnings("UnusedDeclaration")              //We want to offer these methods for future use.
    public int getColumnBoundaryX2(){
        return columnBoundaryX2;
    }
    @SuppressWarnings("UnusedDeclaration")              //We want to offer these methods for future use.
    public int getNumberOfCells(){
        return cells.size();
    }

    /**
     * This is the toString method of the column class
     * @return The method returns all the cells of the column. This is done by going trough each line and add the
     * content of the partitions to a String. After each partition the method adds a "," to the String.
     */
    public String toString(){
        String line = "";
        for(ArrayList<Element>cell : cells){
            for(Element word : cell){
                line = line + word.text() + " ";
            }
            line = line.substring(0, line.length()-1);
            line = line + ", ";
        }
        line = line.substring(0, line.length()-2);
        return line;
    }
}
