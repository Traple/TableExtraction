package program6;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashSet;

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

    public Column2(ArrayList<ArrayList<Element>> cells, double AVGCharDistance){
        this.cells = cells;
        this.AVGCharDistance = AVGCharDistance;

        findColumnBoundaries();
        //System.out.println("I know where my boundaries are: " + columnBoundaryX1 + " and " + columnBoundaryX2);
    }

    //IMPORTANT: We make the assumption that the row reading works correctly. This method doesn't contains checks for this!
    public int getRowDistanceScore(){
        int rowDistanceScore = 0;
        String pos;
        String[] positions;

        for(ArrayList<Element> words : cells){
            int lastY1 = Integer.MAX_VALUE;
            int lastY2 = Integer.MIN_VALUE;
            int distance;
            int rowY2 = 0;
            ArrayList<Integer> rowDistances = new ArrayList<Integer>();
            for(Element word: words){
                pos = word.attr("title");
                positions = pos.split("\\s+");
                int y1 = Integer.parseInt(positions[2]);
                int y2 = Integer.parseInt(positions[4]);

                if(y1 < lastY1){
                    lastY1 = y1;
                }
                if(y2 > lastY2){
                    lastY2 = y2;
                }
            }
            if(rowY2 != 0){
                distance = lastY1 - rowY2;
                rowDistances.add(distance);
            }
            rowY2 = lastY2;
        }

        return rowDistanceScore;
    }

    //This method finds the X1 and X2 of the column.
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

    public void addCell(ArrayList<Element> newCell){
        //System.out.println("Adding: "+ newCell.get(0).text());
        ArrayList<ArrayList<Element>> newCells = new ArrayList<ArrayList<Element>>();
        String pos;
        String[] positions;
        pos = newCell.get(0).attr("title");
        positions = pos.split("\\s+");
        int newCellY1 = Integer.parseInt(positions[2]);
        int newCellY2 = Integer.parseInt(positions[4]);
        boolean notAdded = true;
        int y1=0;
        for(ArrayList<Element> cell : cells){
            pos = cell.get(0).attr("title");
            positions = pos.split("\\s+");
            y1 = Integer.parseInt(positions[2]);
            int y2 = Integer.parseInt(positions[4]);
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

    public int getColumnBoundaryX1(){
        return columnBoundaryX1;
    }
    public int getColumnBoundaryX2(){
        return columnBoundaryX2;
    }
    public boolean fitsInColumn(int x1, int x2){
        return x1 > columnBoundaryX1-(AVGCharDistance/2)&&x2<columnBoundaryX2+(AVGCharDistance/2);
    }                                                                                                             //TODO: These two rules can be a bit more loose. Right now a touching column doesnt trigger. TEST!
    public boolean columnFitsIn(int x1, int x2){
        return x1 < columnBoundaryX1+(AVGCharDistance/2)&&x2>columnBoundaryX2-(AVGCharDistance/2);
    }
    public boolean touchesColumn(int x1, int x2){
        return (x1 < columnBoundaryX1 && x2 >columnBoundaryX1) || (x1 > columnBoundaryX1 && x2 < columnBoundaryX2);
    }

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
    public int getNumberOfCells(){
        return cells.size();
    }
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
}
