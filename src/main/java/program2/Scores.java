package program2;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created for project: TableExtraction
 * In package: program2
 * Created with IntelliJ IDEA.
 * User: Sander van Boom
 * Date: 21-10-13
 * Time: 14:37
 */
public class Scores {
    private Elements spans;
    private CommonMethods CM;
    private ArrayList<Integer> avgWordDistance;
    private ArrayList<Integer> pottentialEndOFTables;

    public Scores(Elements spans){
        this.spans = spans;
        this.CM = new CommonMethods();
        this.pottentialEndOFTables = new ArrayList<Integer>();
        this.avgWordDistance = scoreAvarageWordDistance();


    }

    public ArrayList<Integer> scoreAvarageWordDistance(){
        ArrayList<Integer> avgWordDistance = new ArrayList<Integer>();
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
                    //System.out.println(totalDistance);
                    //System.out.println("The Average distance between words in row "+rows+" is: " + totalDistance/words);
                    avgWordDistance.add(totalDistance/words);
                    //System.out.println(positions[4]);
                    int y2 = Integer.parseInt(positions[4]);
                    pottentialEndOFTables.add(y2);
                    totalDistance = 0;
                    words = 0;
                }
            }
            previousX2 = Integer.parseInt(positions[3]);
        }
        //System.out.println(avgWordDistance);
        return avgWordDistance;
    }

    //TODO: findEndOfTable should be influenced by many more scores except for just the avg distance between words.
    //TODO: Create parameter for the distance between words 200.
    public int findEndOfTable(){
        boolean readingData = false;
        int counter = 0;
        System.out.println(avgWordDistance);
        System.out.println(pottentialEndOFTables);
        for(int distanceBetweenWordsInRow : avgWordDistance){
            if(distanceBetweenWordsInRow >200){
                readingData = true;
            }
            else if(readingData){
                //the end of the table!
                return pottentialEndOFTables.get(counter-2);
            }
            counter++;
        }
    return pottentialEndOFTables.get(pottentialEndOFTables.size()-1);
    }
}
