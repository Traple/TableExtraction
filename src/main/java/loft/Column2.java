package loft;

import org.jsoup.nodes.Element;

import java.util.ArrayList;

/**
 * Created for project: TableExtraction
 * In package: loft.program
 * Created with IntelliJ IDEA.
 * User: Sander van Boom
 * Date: 28-11-13
 * Time: 11:24
 */                        /*
public class Column2 {
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
                             */