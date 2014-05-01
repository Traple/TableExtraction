package program8;

import java.io.*;
import java.util.logging.Logger;


//The configuration class provides methods that process the configuration file.
public class Configuration {

    private String pathToImageMagick;
    private String pathToTesseract;
    private String pathToTesseractConfigFile;
    private int allowedHeaderIterations;
    private double horizontalThresholdModifier;
    private double verticalThresholdModifier;
    private double imageMagickResolution;
    private int allowedHeaderSize;
    public static Logger LOGGER = Logger.getLogger(Configuration.class.getName());

    /**
     * This constructor reads the configuration file and uses it's content to set the local variables.
     * @param pathToConfigurationFile The path to the configuration file, as given by the user on the -C argument.
     * @throws java.io.IOException
     */
    public Configuration(String pathToConfigurationFile) throws IOException {
        File file = new File(pathToConfigurationFile);
        if (!file.exists() || file.isDirectory()) {
            LOGGER.info("The path to the configuration file is incorrect or is a directory. System shutting down.");
            System.out.println("The path to the configuration file is incorrect or is a directory. System shutting down.");
            System.exit(1);
        }
        FileInputStream fis = new FileInputStream(pathToConfigurationFile);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

        this.pathToImageMagick = reader.readLine();
        if(pathToImageMagick == null){
            LOGGER.info("The configuration file does not contain all the fields. System shutting down.");
            System.out.println("The configuration file does not contain all the fields. " +
                    "Please check Readme file or check the example configuration file for help on making a good configuration file." +
                    " System shutting down.");
            System.exit(1);
        }
        this.pathToTesseract = reader.readLine();
        if(pathToTesseract == null){
            LOGGER.info("The configuration file does not contain all the fields. System shutting down.");
            System.out.println("The configuration file does not contain all the fields. " +
                    "Please check Readme file or check the example configuration file for help on making a good configuration file." +
                    " System shutting down.");
            System.exit(1);
        }
        this.pathToTesseractConfigFile = reader.readLine();
        if(pathToTesseractConfigFile == null){
            LOGGER.info("The configuration file does not contain all the fields. System shutting down.");
            System.out.println("The configuration file does not contain all the fields. " +
                    "Please check Readme file or check the example configuration file for help on making a good configuration file." +
                    " System shutting down.");
            System.exit(1);
        }
        try{
            this.imageMagickResolution = Double.parseDouble(reader.readLine());
            this.horizontalThresholdModifier = Double.parseDouble(reader.readLine());
            this.verticalThresholdModifier = Double.parseDouble(reader.readLine());
            this.allowedHeaderSize = Integer.parseInt(reader.readLine());
            this.allowedHeaderIterations = Integer.parseInt(reader.readLine());
        }
        catch(NullPointerException e){
            LOGGER.info("One of the parameters in the configuration file does not contain an appropriate value. System shutting down.");
            System.out.println("One of the parameters in the configuration file does not contain an appropriate value. " +
                    "Please check Readme file or check the example configuration file for help on making a good configuration file. " +
                    "System shutting down.");
            System.exit(1);
        }
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

    public double getImageMagickResolution() {
        return imageMagickResolution;
    }
}

