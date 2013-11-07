package program6;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;


//The configuration class provides methods that process the configuration file.
public class Configuration {
    private String pathToImageMagick;
    private String pathToTesseract;
    private String pathToTesseractConfigFile;

    /**
     * This constructor reads the configuration file and uses it's content to set the local variables.
     * @param pathToConfigurationFile The path to the configuration file, as given by the user on the -C argument.
     * @throws IOException
     */
    public Configuration(String pathToConfigurationFile) throws IOException {
        FileInputStream fis = new FileInputStream(pathToConfigurationFile);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

        this.pathToImageMagick = reader.readLine();
        this.pathToTesseract = reader.readLine();
        this.pathToTesseractConfigFile = reader.readLine();
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
}

