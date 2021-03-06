package program4;

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
    private double spaceDistance;
    private double distanceConstant;

    public Scores(Elements spans){
        this.spans = spans;
        this.CM = new CommonMethods();
        this.pottentialEndOFTables = new ArrayList<Integer>();
        this.pottentialBeginOFTables = new ArrayList<Integer>();
        this.distanceConstant = 2;

        System.out.println("AVG CharLength: " + findSpaceDistance());
        this.spaceDistance = findSpaceDistance();
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

    public int findEndOfTable(){
        boolean readingData = false;
        int counter = 0;
        System.out.println(avgWordDistance);
        System.out.println(pottentialEndOFTables);
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

    public double findSpaceDistance(){
        double totalCharLength = 0.0;
        for(Element span : spans){
            String pos = span.attr("title");
            String[] positions = pos.split("\\s+");
            String word = span.text();
            int X1 = Integer.parseInt(positions[1]);
            int X2 = Integer.parseInt(positions[3]);
            int length = X2 -X1;
            if(word.length() > 0){
                double charLength = length/word.length();
                totalCharLength = totalCharLength + charLength;
            }
        }
        return totalCharLength/spans.size();
    }

    /**
     * This method will return the information of the scores. This can then be parsed to the logger.
     * @return This software returns a string contain the scores and values from the scoring process.
     */
    public String getScoreInformation(){
        String scoreInformation = "";
        return scoreInformation;
    }
}
