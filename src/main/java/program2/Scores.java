package program2;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created for project: TableExtraction
 * In package: program2
 * Created with IntelliJ IDEA.
 * User: Sander van Boom
 * Date: 21-10-13
 * Time: 14:37
 */
public class Scores {
    Elements spans;
    CommonMethods CM;
    public Scores(Elements spans){
        this.spans = spans;
        this.CM = new CommonMethods();
    }

    public int score(){
        double avgWordDistance = 0.0;
        int score = 0;
        int previousX2 = 0;
        int totalDistance = 0;
        int rows = 0;
        int words = 0;
        for(Element span : spans){
            String pos = span.attr("title");
            String[] positions = pos.split("\\s+");
            int currentX1 = Integer.parseInt(positions[1]);
            if(span != spans.get(0)){
                if(previousX2 < currentX1){
                    totalDistance = totalDistance + CM.calcDistance(previousX2, currentX1);
                    words++;
                }
                else if(words > 0){
                    rows++;
                    System.out.println(totalDistance);
                    System.out.println("The Average distance between words in row "+rows+" is: " + totalDistance/words);
                    totalDistance = 0;
                    words = 0;
                }
                else{
                    break;
                }
            }

            previousX2 = Integer.parseInt(positions[3]);
        }

        return score;
    }
}
