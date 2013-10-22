package program2;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 *
 */
public class Scores {
    private Elements spans;
    private CommonMethods CM;
    private ArrayList<Integer> avgWordDistance;
    private ArrayList<Integer> pottentialEndOFTables;
    private ArrayList<Integer> pottentialBeginOFTables;
    private double distanceTreshold;

    public Scores(Elements spans){
        this.spans = spans;
        this.CM = new CommonMethods();
        this.pottentialEndOFTables = new ArrayList<Integer>();
        this.pottentialBeginOFTables = new ArrayList<Integer>();
        this.avgWordDistance = scoreAvarageWordDistance();
        System.out.println("AVGDistance Treshold: "+findAVGDistanceTreshold());

    }

    /**
     *
     * @return
     */
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
                words++;
                if(previousX2 < currentX1){
                    totalDistance = totalDistance + CM.calcDistance(previousX2, currentX1);

                }

                else if(words > 0){
                    rows++;
                    //System.out.println(totalDistance);
                    //System.out.println("The Average distance between words in row "+rows+" is: " + totalDistance/words);
                    avgWordDistance.add(totalDistance/words);
                    //System.out.println(positions[4]);
                    int y2 = Integer.parseInt(positions[4]);
                    pottentialBeginOFTables.add(y2);
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
    public int findEndOfTable(){
        boolean readingData = false;
        int counter = 0;
        System.out.println(avgWordDistance);
        System.out.println(pottentialEndOFTables);
        for(int distanceBetweenWordsInRow : avgWordDistance){
            if(distanceBetweenWordsInRow >=distanceTreshold){
                readingData = true;
            }
            else if(readingData&&distanceBetweenWordsInRow>0){
                //the end of the table!
                return pottentialEndOFTables.get(counter-2);
            }
            counter++;
        }
        return pottentialEndOFTables.get(pottentialEndOFTables.size()-1);
    }

    /**
     *
     * @return
     */
    public int findBeginOfTable(){
        boolean readingTitle = false;
        int counter = 0;
        //System.out.println(avgWordDistance);
        //System.out.println(pottentialBeginOFTables);
        for(int distanceBetweenWordsInRow : avgWordDistance){
            if(distanceBetweenWordsInRow <=distanceTreshold){
                readingTitle = true;
            }
            else if(readingTitle && counter>=2){
                //the end of the table!
                return pottentialBeginOFTables.get(counter-2);
            }
            else{
                return pottentialBeginOFTables.get(counter-1);
            }
            counter++;
        }
        return pottentialBeginOFTables.get(pottentialBeginOFTables.size()-1);
    }

    /**
     *
     * @return
     */
    public double findAVGDistanceTreshold(){
        int totalLength = 0;
        for(Element span : spans){
            String pos = span.attr("title");
            String[] positions = pos.split("\\s+");
            int X1 = Integer.parseInt(positions[1]);
            int X2 = Integer.parseInt(positions[3]);
            int length = X2 -X1;
            totalLength = totalLength + length;
        }
        double avgWordLength = totalLength/spans.size();
        this.distanceTreshold = avgWordLength*1.5;
        return (avgWordLength*1.5);
    }

    public double getDistanceTreshold(){
        return distanceTreshold;
    }
}
