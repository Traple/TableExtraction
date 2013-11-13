package program6;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * The column class contains methods that can be used to find headers and gain information on a column basis.
 * For example when looking for headers we might want to take the space between rows into account. the column class contains
 * a method to check if this is viable by returning an internal score.
 */
public class Column2 {
    private ArrayList<ArrayList<Element>> cells;

    public Column2(ArrayList<ArrayList<Element>> cells){
        System.out.println("I'm a column.");
        this.cells = cells;
    }

    //TODO: Finish this method, there are some mistakes in it (look at the first element). For the rest it works!
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
}
