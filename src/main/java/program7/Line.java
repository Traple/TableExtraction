package program7;


import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class Line {
    private Elements words;
    private double lineThreshold;
    private ArrayList<ArrayList<Element>> clusters;
    private double thresholdModifier;
    private int Y1OfFirstWord;
    private int Y1OfLastWord;
    private ArrayList<Integer> distances;
    private double averageY1;
    private double averageY2;

    /**
     * This is the constructor of the Line class.
     * It sets three internal variables (words, LineThreshold and the ThresholdModifier)
     * The ThresholdModifier is hardcoded to 2.
     * @param words words are the words that are in this line. It is a list of HTML Elements derived from the OCR
     * @param lineThreshold the threshold that was calculated in the average distance of words in the page class.
     */
    public Line(Elements words, double lineThreshold, double lineThresholdModifier){
        this.words = words;
        this.lineThreshold = lineThreshold;
        this.thresholdModifier = lineThresholdModifier;
        ClusterColumns();

        String[] positions;
        String pos = words.get(0).attr("title");
        positions = pos.split("\\s+");
        this.Y1OfFirstWord = Integer.parseInt(positions[2]);

        pos = words.get(words.size()-1).attr("title");
        positions = pos.split("\\s+");
        this.Y1OfLastWord = Integer.parseInt(positions[2]);
        setAverageY1andY2();
    }

    /**
     * In this case you dont want to create clusters.
     * @param words
     */
    public Line(Elements words){
        this.words = words;
        String[] positions;
        String pos = words.get(0).attr("title");
        positions = pos.split("\\s+");
        this.Y1OfFirstWord = Integer.parseInt(positions[2]);

        pos = words.get(words.size()-1).attr("title");
        positions = pos.split("\\s+");
        this.Y1OfLastWord = Integer.parseInt(positions[2]);
        setAverageY1andY2();
    }

    /**
     * This method creates partitions (clusters) in the line by checking if the distances between words in the line is
     * bigger then the average distance of a character * thresholdModifier (default : 2). If so the words are saved in
     * a new partition and and the results are stored in the private clusters variable.
     * The collected distances are also saved in a private variable for validation purposes.
     */
    private void ClusterColumns(){
        String pos = words.get(0).attr("title");
        String[] positions;
        positions = pos.split("\\s+");
        int lastX2 = Integer.parseInt(positions[3]);
        ArrayList<Element> cluster = new ArrayList<Element>();
        ArrayList<Integer> distances = new ArrayList<Integer>();
        clusters = new ArrayList<ArrayList<Element>>();

        for(Element word : words){
            pos = word.attr("title");
            positions = pos.split("\\s+");
            int x1 = Integer.parseInt(positions[1]);
            int x2 = Integer.parseInt(positions[3]);
            int distance = -(lastX2 - x1);

            if((distance) >lineThreshold*thresholdModifier){
                distances.add(distance);
                clusters.add(cluster);
                cluster = new ArrayList<Element>();
                cluster.add(word);
            }
            else{
                cluster.add(word);
            }
            lastX2 = x2;
        }
        if(clusters.size()>=1&&!clusters.get(0).equals(cluster)){
            clusters.add(cluster);
        }
        this.distances = distances;
    }

    /**
     * This method sets the average Y coordinates (top and bottom positions) of the line.
     * This can be used to give a more accurate vertical position of the line.
     * It stores the results in the local variables averageY1 and averageY2.
     */
    private void setAverageY1andY2(){
        String pos;
        String[] positions;
        int totalY1 = 0;
        int totalY2 = 0;
        for(Element word : words){
            pos = word.attr("title");
            positions = pos.split("\\s+");
            int y1 = Integer.parseInt(positions[2]);
            totalY1 = totalY1 + y1;
            int y2 = Integer.parseInt(positions[4]);
            totalY2 = totalY2 + y2;
        }
        this.averageY1 = totalY1/words.size();
        this.averageY2 = totalY2/words.size();
    }
    //~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-
    //Getters:

    /**
     * This method returns the left boundary of the line. (the start of the line)
     * It is public because it needs to be called by the Table2 class when replacing lines with missing partitions.
     * @param cluster A partition in the line.
     * @return the left position of the given partition.
     */
    public static int getClusterX1(ArrayList<Element> cluster){
            int clusterBoundaryX1 = Integer.MAX_VALUE;
            String pos;
            String[] positions;

            Element firstWordInCell = cluster.get(0);

            pos = firstWordInCell.attr("title");
            positions = pos.split("\\s+");
            int x1 = Integer.parseInt(positions[1]);
            if(x1 < clusterBoundaryX1){
                clusterBoundaryX1 = x1;
            }
        return clusterBoundaryX1;
        }

    /**
     * Similar of getClusterX1, this method returns the right position (boundary) of the given partition.
     * @param cluster A list containing the words of this partition.
     * @return the right position of the given partition.
     */
    public static int getClusterX2(ArrayList<Element> cluster){
        int clusterBoundaryX2 = Integer.MIN_VALUE;
        String pos;
        String[] positions;

        Element lastWordInCell = cluster.get(cluster.size()-1);

        pos = lastWordInCell.attr("title");
        positions = pos.split("\\s+");
        int x2 = Integer.parseInt(positions[3]);
        if(x2 > clusterBoundaryX2){
            clusterBoundaryX2 = x2;
        }
        return clusterBoundaryX2;
    }

    public ArrayList<ArrayList<Element>> getClusters(){
        return clusters;
    }
    public int getClusterSize(){
        return clusters.size();
    }
    public int getY1OfFirstWord(){
        return Y1OfFirstWord;
    }
    public int getY1OfLastWord(){
        return Y1OfLastWord;
    }
    public double getAverageY1(){
        return averageY1;
    }
    public double getAverageY2(){
        return averageY2;
    }
    public ArrayList<Integer> getDistances(){
        return distances;
    }
    public double getDistanceThreshold(){
        return thresholdModifier * lineThreshold;
    }
    public Cell getCellObject(){
        ArrayList<Element> wordsAsList = new ArrayList<Element>();
        for(Element word : words){
            wordsAsList.add(word);
        }
        return new Cell(wordsAsList);
    }

/*    public ArrayList<Cell> getCellObjects(){
        ArrayList<Cell> cellObjects = new ArrayList<Cell>();
        for(ArrayList<Element> cluster : clusters){
            Cell cell = new Cell(cluster);
            cellObjects.add(cell);
        }
        return cellObjects;
    }*/

    /**
     * This is the toString method for the Line class.
     * The method returns the content of the line by looping trough each word(.text()) and adding that to the content.
     * @return the method returns the content of the line.
     */
    public String toString(){
        String line = "";
        for(Element word : words){
            line = line + word.text() + " ";
        }
        return line;
    }
}
