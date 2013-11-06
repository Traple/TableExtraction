package program5;

import java.io.*;

/**
 * The configuration class provides methods that process the configuration file.
 */
public class Configuration {
    private String pathToImageMagick;
    private String pathToTesseract;
    private String pathToTesseractConfigFile;

    public Configuration(String pathToConfigurationFile) throws IOException {
        FileInputStream fis = null;
        BufferedReader reader = null;

        System.out.println("Reading the config file.");

        fis = new FileInputStream(pathToConfigurationFile);
        reader = new BufferedReader(new InputStreamReader(fis));

        this.pathToImageMagick = reader.readLine();
        this.pathToTesseract = reader.readLine();
        this.pathToTesseractConfigFile = reader.readLine();
        reader.close();
        fis.close();
    }
    public String getPathToImageMagick(){
        return pathToImageMagick;
    }
    public String getPathToTesseract(){
        return pathToTesseract;
    }
    public String getPathToTesseractConfigFile(){
        return pathToTesseractConfigFile;
    }
}

