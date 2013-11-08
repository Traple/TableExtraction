package program6;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

//This class is used to score the table for it's X1 and X2 positions.
public class Scores {
    private Elements spans;
    private CommonMethods CM;
    private ArrayList<Integer> avgWordDistance;
    private ArrayList<Integer> pottentialEndOFTables;
    private ArrayList<Integer> pottentialBeginOFTables;
    private double distanceTreshold;
    private double spaceDistance;
    private double distanceConstant;

    /**
     * The constructor sets the private variables for this class and calls some it's own methods.
     * @param spans the current HTML words of this table.
     */
    public Scores(Elements spans, double spaceDistance){
        this.spans = spans;
        this.CM = new CommonMethods();
        this.pottentialEndOFTables = new ArrayList<Integer>();
        this.pottentialBeginOFTables = new ArrayList<Integer>();
        this.distanceConstant = 4;

        System.out.println("AVG CharLength: " + spaceDistance);
        this.spaceDistance = spaceDistance;
        System.out.println("AVGDistance Treshold: " + findAVGDistanceTreshold());
        this.avgWordDistance = scoreAvarageWordDistance();
    }

    /**
     * This method scores the distance between words by checking their X locations.
     * @return This method returns a list the distance between words in pixels
     */
    public ArrayList<Integer> scoreAvarageWordDistance(){
        ArrayList<Integer> avgWordDistance = new ArrayList<Integer>();
        int previousX2 = 0;
        int totalDistance = 0;
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
                    avgWordDistance.add(totalDistance/words);
                    int y1 = Integer.parseInt(positions[2]);
                    int y2 = Integer.parseInt(positions[4]);
                    pottentialBeginOFTables.add(y1);
                    pottentialEndOFTables.add(y2);
                    totalDistance = 0;
                    words = 0;
                }
            }
            previousX2 = Integer.parseInt(positions[3]);
        }
        return avgWordDistance;
    }

    //TODO: Use the information from the non semantic table content finder to detect the end of the table.
    /**
     * This method will use the distance between words to find the end of a table.
     * WARNING: This method is one of the key reasons we get false negatives when it comes to extracting the whole table.
     * Note: This method is also used to reduce the amount of false positives.
     * @return the X location that marks the end of the table.
     */
    public int findEndOfTable(){
        boolean readingData = false;
        int counter = 0;
        for(int distanceBetweenWordsInRow : avgWordDistance){
            if(distanceBetweenWordsInRow >=distanceTreshold){
                readingData = true;
            }
            else if(readingData&&distanceBetweenWordsInRow>0&&counter>2){
                //the end of the table!
                return pottentialEndOFTables.get(counter-2);
            }
            counter++;
        }
        return pottentialEndOFTables.get(pottentialEndOFTables.size()-1);
    }

    /**
     * This method tries to find the beginning of the table based on the average distance threshold.
     * @return This method returns the beginning of the table
     */
    public int findBeginOfTable(){
        boolean readingTitle = false;
        int counter = 0;
        for(int distanceBetweenWordsInRow : avgWordDistance){
            if(distanceBetweenWordsInRow <=distanceTreshold){
                readingTitle = true;
            }
            else if(readingTitle && counter>=2){
                //the end of the table!
                return pottentialBeginOFTables.get(counter-2);
            }
            else if(counter>0){
                return pottentialBeginOFTables.get(counter-1);
            }
            counter++;
        }
        return pottentialBeginOFTables.get(pottentialBeginOFTables.size()-1);
    }

    /**
     * This method returns the treshold that is being used in finding the beginning and end of the table.
     * It takes the average distance of a character and multiplies it with the number 4.
     * @return the average character distance treshold.
     */
    public double findAVGDistanceTreshold(){
        this.distanceTreshold = spaceDistance*distanceConstant;
        return (spaceDistance*distanceConstant);
    }

    public double getDistanceConstant(){
        return distanceConstant;
    }
}
