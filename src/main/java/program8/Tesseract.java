package program8;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

//This class calls the commandline to call one of the two dependencies: Tesseract.
//Tesseract is used for the OCR of the PNG files created by ImageMagick.
public class Tesseract {

    public static Logger LOGGER = Logger.getLogger(Tesseract.class.getName());
    private String[] command;

    /**
     * This constructor creates the commandline we have to use in order to call Tesseract
     * @param pathToTesseract the path to Tesseract
     * @param workspace The used workspace, containing the .png files.
     * @param ID The ID of the file currently being analysed
     * @param page The current page that is being analysed
     * @param pathToConfig path to the configuration file of Tesseract
     */
    public Tesseract(String pathToTesseract, String workspace, String ID, String page, String pathToConfig){
        String input = workspace + "/" + ID + "-" + page + ".png";
        String output = workspace + "/" + ID + "-" + page;
        this.command = new String[] {pathToTesseract, input, output,"hocr",pathToConfig};
    }

    /**
     * this void method runs the command created in the constructor.
     */
    public void runTesseract(){
        LOGGER.info("Trying to run command: " + command[0]+" "+command[1]+" "+command[2]+" "+command[3]+" "+command[4]);
        System.out.println("Trying to run command: " + Arrays.toString(command));
        try {
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec(command);
            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line;
            while((line=input.readLine()) != null) {
                System.out.println(line);
            }

            int exitVal = pr.waitFor();
            System.out.println("Exited with error code "+exitVal);
        }
        catch(Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }

    /**
     * This method finds the created HTML files in the working directory.
     * @param workingDirectory the directory that has to be analysed.
     * @param ID the current file being analysed
     * @return a list with the html files.
     */
    public static ArrayList<File> findHTMLFilesInWorkingDirectory(String workingDirectory, String ID){
        ArrayList<File> HTMLFiles = new ArrayList<File>();
        File dir = new File(workingDirectory);
        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".html");
            }
        });
        for(File file : files){
            if(file.getName().startsWith(ID)){
                System.out.println(file.getAbsolutePath());
                HTMLFiles.add(file);
            }
        }
        return HTMLFiles;
    }
}

