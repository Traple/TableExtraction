package program7;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * This class stores validation variables and contains methods to calculate the significance of these variables
 * as well as getters and setters for each variable.
 */
public class Validation {

    public static Logger LOGGER = Logger.getLogger(Validation.class.getName());
    private ArrayList<Double> clusterCertainty;                                 //This variable holds the information of
                                                                               // how certain the program is about each column being a column.
    private int mostFrequentNumberOfClusters;
    private double lineThreshold;
    private double averageDistanceBetweenRows;
    private ArrayList<Double> titleConfidence;
    private int highestAmountOfClusters;
    private int highestAmountOfClustersOccurrences;
    private int cellsWithMissingDataAdded;
    private ArrayList<Cell> cellsWithMissingDataAddedObjects;
    private boolean falsePositive;

    /**
     * This is the constructor for the Validation class. It sets the private variables of this object.
     */
    public Validation(){
        this.titleConfidence = new ArrayList<Double>();
        this.clusterCertainty = new ArrayList<Double>();
        this.falsePositive = false;
    }

    //------------------------------------------------------------------------------------------------------------------
    //Setters:
    public void setClusterCertainty(ArrayList<Integer> clusterDistances, double AVGCharacterDistanceThreshold){
        double certainty;
        ArrayList<Double> clusterCertainties = new ArrayList<Double>();
        for(int clusterDistance : clusterDistances){
            certainty = clusterDistance/AVGCharacterDistanceThreshold;
            clusterCertainties.add(certainty);
        }
        this.clusterCertainty = clusterCertainties;
    }
    public void setCellsWithMissingDataAddedScores(ArrayList<Cell> cells){
        this.cellsWithMissingDataAddedObjects = cells;
    }
    public void calculateTitleConfidence(double averageDistanceBetweenRows, double distanceBetweenRow, double lineDistanceModifier){
        double certainty = distanceBetweenRow/(averageDistanceBetweenRows * lineDistanceModifier);
        this.titleConfidence.add(certainty);
    }
    public void setMostFrequentNumberOfClusters(int mostFrequentNumberOfClusters){
        this.mostFrequentNumberOfClusters = mostFrequentNumberOfClusters;
    }
    public void setHighestAmountOfClusters(int highestAmountOfClusters){
        this.highestAmountOfClusters = highestAmountOfClusters;
    }
    public void setHighestAmountOfClustersOccurrences(int highestAmountOfClustersOccurrences){
        this.highestAmountOfClustersOccurrences = highestAmountOfClustersOccurrences;
    }
    public void setLineThreshold(double lineThreshold){
        this.lineThreshold = lineThreshold;
    }
    public void setAverageDistanceBetweenRows(double averageDistanceBetweenRows){
        this.averageDistanceBetweenRows = averageDistanceBetweenRows;
    }
    public void setCellsWithMissingDataAdded(int cellsWithMissingDataAdded){
        this.cellsWithMissingDataAdded = cellsWithMissingDataAdded;
    }
    public void setFalsePositive(boolean falsePositive){
        this.falsePositive = falsePositive;
    }

    //------------------------------------------------------------------------------------------------------------------
    //getters:
    @SuppressWarnings("UnusedDeclaration")          //For when we need to use the validation scores in actual software
    public ArrayList<Double> getClusterCertainty(){
        return clusterCertainty;
    }
    @SuppressWarnings("UnusedDeclaration")          //For when we need to use the validation scores in actual software
    public int getMostFrequentNumberOfClusters(){
        return mostFrequentNumberOfClusters;
    }
    @SuppressWarnings("UnusedDeclaration")          //For when we need to use the validation scores in actual software
    public double getLineThreshold(){
        return lineThreshold;
    }
    @SuppressWarnings("UnusedDeclaration")          //For when we need to use the validation scores in actual software
    public double getAverageDistanceBetweenRows(){
        return averageDistanceBetweenRows;
    }
    public ArrayList<Double> getTitleConfidence(){
        return titleConfidence;
    }
    public boolean getFalsePositive(){
        return falsePositive;
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
        String content = "";
        content = content + "Column Confidence: " + clusterCertainty + "\n";
        content = content + "Most Frequently found number of clusters in table: " + mostFrequentNumberOfClusters+"\n";
        content = content + "Highest amount of clusters found in the table: " + highestAmountOfClusters + "\n";
        content = content + "Which occurred: " + highestAmountOfClustersOccurrences + " times\n";
        content = content + "Which was calculated using the following clusterThreshold: " + lineThreshold+"\n";
        content = content + "Cells with missing data added: " + cellsWithMissingDataAdded + "\n";
        if(cellsWithMissingDataAdded >0){
            content = content + "Which was done on the following scores: " + cellsWithMissingDataAddedObjects + "\n";
        }
        content = content + "The title was calculated using the average distance between rows, which was: " + averageDistanceBetweenRows + "\n";
        if(titleConfidence.size() > 0){
            content = content + "A piece of the title was added as header. This was done with the following confidence: " + titleConfidence + "\n";
        }
        LOGGER.info(content);
        return content;
    }

    //TODO: Improve column confidence, make it take to little columns into account (which happens quite often on low quality tables).
    //TODO: Possible create a fix for the first column having incomplete lines (with fix being incomplete validation).
    //TODO: Add a method to check for the whitespaces in a column so we can increase column confidence
    /**
     * This method creates valid XML from the validation scores.
     * @return A string containing the different validation scores.
     */
    public String toXML(){
        String content = "";
        content = content + "    <validation>\n";
        content = content + "        <columnConfidence>" + clusterCertainty + "</columnConfidence>\n";
        content = content + "        <mostFrequentlyNumberOfClusters>" + mostFrequentNumberOfClusters+"</mostFrequentlyNumberOfClusters>\n";
        content = content + "        <highestAmountOfClusters>" + highestAmountOfClusters + "</highestAmountOfClusters>\n";
        content = content + "        <highestAmountOfClustersOccurrences>" + highestAmountOfClustersOccurrences + "</highestAmountOfClustersOccurrences>\n";
        content = content + "        <clusterThreshold>" + lineThreshold+"</clusterThreshold>\n";
        content = content + "        <cellsWithMissingDataAdded>" + cellsWithMissingDataAdded +"</cellsWithMissingDataAdded>\n";
        if(cellsWithMissingDataAdded > 0){
            content = content + "        <cellsWithMissingDataAddedScores>" +CommonMethods.changeIllegalXMLCharacters(cellsWithMissingDataAddedObjects.toString()) + "</cellsWithMissingDataAddedScores>\n";
        }
        content = content + "        <averageDistanceBetweenRows>" + averageDistanceBetweenRows + "</averageDistanceBetweenRows>\n";
        if(titleConfidence.size() > 0){
            content = content + "        <titleConfidence>" + titleConfidence + "</titleConfidence>\n";
        }
        content = content + "        <falsePositive>" + falsePositive + "</falsePositive>\n";
        content = content + "    </validation>\n";
        return content;
    }
}
