package program6;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * This class stores validation variables and contains methods to calculate the significance of these variables
 * as well as getters and setters for each variable.
 */
public class Validation {
    //This variable holds the information of how certain the program is about each column being a column.
    public static Logger LOGGER = Logger.getLogger(Validation.class.getName());
    private ArrayList<Double> clusterCertainty;
    private int mostFrequentNumberOfClusters;
    private double lineThreshold;
    private double averageDistanceBetweenRows;
    private ArrayList<Double> titleConfidence;

    public Validation(){
        this.titleConfidence = new ArrayList<Double>();
        this.clusterCertainty = new ArrayList<Double>();
    }

    /**
     * The toString method creates a summary of all the validation that has been done trough-out the program.
     * It specifies different aspects of the final table. For every variable in the validation it returns a new line in the
     * String.
     * NOTE: The validation class also offers getters for the individual variables. It is highly recommended that these are used
     * for getting the individual variables for compatibility issues!
     * @return "Column Confidence: [confidenceScore between cluster 1 and cluster 2, confidenceScore between cluster 2 and cluster 3]"
     */
    public String toString(){
        String content = new String();
        content = content + "Column Confidence: " + clusterCertainty + "\n";
        content = content + "Most Frequently found number of clusters in table: " + mostFrequentNumberOfClusters+"\n";
        content = content + "Which was calculated using the following clusterThreshold: " + lineThreshold+"\n";
        content = content + "The title was calculated using the average distance between rows, which was: " + averageDistanceBetweenRows + "\n";
        if(titleConfidence.size() > 0){
            content = content + "A piece of the title was added as header. This was done with the following confidence: " + titleConfidence + "\n";
        }
        LOGGER.info(content);
        return content;
    }

    public void setMostFrequentNumberOfClusters(int mostFrequentNumberOfClusters){
        this.mostFrequentNumberOfClusters = mostFrequentNumberOfClusters;
    }
    public void setClusterCertainty(ArrayList<Integer> clusterDistances, double AVGCharacterDistanceThreshold){
        double certainty;
        ArrayList<Double> clusterCertainties = new ArrayList<Double>();
        for(int clusterDistance : clusterDistances){
            certainty = clusterDistance/AVGCharacterDistanceThreshold;
            clusterCertainties.add(certainty);
        }
        this.clusterCertainty = clusterCertainties;
    }
    public void setLineThreshold(double lineThreshold){
        this.lineThreshold = lineThreshold;
    }
    public void setAverageDistanceBetweenRows(double averageDistanceBetweenRows){
        this.averageDistanceBetweenRows = averageDistanceBetweenRows;
    }
    public void calculateTitleConfidence(double averageDistanceBetweenRows, double distanceBetweenRow, double lineDistanceModifier){
        double certainty = distanceBetweenRow/(averageDistanceBetweenRows * lineDistanceModifier);
        this.titleConfidence.add(certainty);
    }



    //------------------------------------------------------------------------------------------------------------------
    public ArrayList<Double> getClusterCertainty(){
        return clusterCertainty;
    }

    public int getMostFrequentNumberOfClusters(){
        return mostFrequentNumberOfClusters;
    }

    public double getLineThreshold(){
        return lineThreshold;
    }
    public double getAverageDistanceBetweenRows(){
        return averageDistanceBetweenRows;
    }
    public String toXML(){
        String content = new String();
        content = content + "    <validation>\n";
        content = content + "        <columnConfidence>" + clusterCertainty + "</columnConfidence>\n";
        content = content + "        <mostFrequentlyNumberOfClusters>" + mostFrequentNumberOfClusters+"</mostFrequentlyNumberOfClusters>\n";
        content = content + "        <clusterThreshold>" + lineThreshold+"</clusterThreshold>\n";
        content = content + "        <averageDistanceBetweenRows>" + averageDistanceBetweenRows + "</averageDistanceBEtweenRows>\n";
        if(titleConfidence.size() > 0){
            content = content + "<titleConfidence>" + titleConfidence + "</titleConfidence>\n";
        }
        content = content + "    </validation>\n";
        return content;
    }
}
