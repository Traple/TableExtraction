package program8;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;


//The configuration class provides methods that process the configuration file.
public class Configuration {
    private final double horizontalThresholdModifier;
    private final double verticalThresholdModifier;
    private final int allowedHeaderSize;
    private String pathToImageMagick;
    private String pathToTesseract;
    private String pathToTesseractConfigFile;
    private int allowedHeaderIterations;

    /**
     * This constructor reads the configuration file and uses it's content to set the local variables.
     * @param pathToConfigurationFile The path to the configuration file, as given by the user on the -C argument.
     * @throws java.io.IOException
     */
    public Configuration(String pathToConfigurationFile) throws IOException {
        FileInputStream fis = new FileInputStream(pathToConfigurationFile);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

        this.pathToImageMagick = reader.readLine();
        this.pathToTesseract = reader.readLine();
        this.pathToTesseractConfigFile = reader.readLine();
        this.horizontalThresholdModifier = Double.parseDouble(reader.readLine());
        this.verticalThresholdModifier = Double.parseDouble(reader.readLine());
        this.allowedHeaderSize = Integer.parseInt(reader.readLine());
        this.allowedHeaderIterations = Integer.parseInt(reader.readLine());
        reader.close();
        fis.close();
    }

    //~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-
    //Getters of this method:

    public String getPathToImageMagick(){
        return pathToImageMagick;
    }
    public String getPathToTesseract(){
        return pathToTesseract;
    }
    public String getPathToTesseractConfigFile(){
        return pathToTesseractConfigFile;
    }
    public double getHorizontalThresholdModifier(){
        return horizontalThresholdModifier;
    }
    public double getVerticalThresholdModifier(){
        return verticalThresholdModifier;
    }
    public int getAllowedHeaderSize(){
        return allowedHeaderSize;
    }
    public int getAllowedHeaderIterations(){
        return allowedHeaderIterations;
    }
}

