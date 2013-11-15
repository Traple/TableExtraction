package program6;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * This class stores validation variables and contains methods to calculate the significance of these variables
 * as well as getters and setters for each variable.
 */
public class Validation {
    //This variable holds the information of how certain the program is about each column being a column.
    private ArrayList<Double> clusterCertainty = new ArrayList<Double>();

    public static Logger LOGGER = Logger.getLogger(Validation.class.getName());

    public Validation(){

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
        content = content + "Column Confidence: " + clusterCertainty;
        LOGGER.info(content);
        return content;
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
    public ArrayList<Double> getClusterCertainty(){
        return clusterCertainty;
    }

}
