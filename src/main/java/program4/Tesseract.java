package program4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Logger;

public class Tesseract {


    public static Logger LOGGER = Logger.getLogger(Tesseract.class.getName());
    private String command;
    private String[] command2;

    public Tesseract(String fileLocation, String outputLocation){
        //Windows:
        //String path = "\"C:\\Program Files (x86)\\Tesseract-OCR\\tesseract.exe\"";
        //Linux:
        String path = "/d/as2/s/tesseract-ocr/bin/tesseract";
        //Windows:
        //String pathToConfig = "\"C:\\Users\\Sander van Boom\\Downloads\\config.txt\"";
        //Linux:
        String pathToConfig = "config.txt";
        String[] command2 = {"tcsh","-c",path, fileLocation, outputLocation,"hocr" ,pathToConfig};
        this.command2 = command2;
        this.command = path + " " + fileLocation + " " + outputLocation + " hocr " + pathToConfig;
    }

    public void runTesseract(){
        LOGGER.info("Tring to run command: " + command);
        System.out.println("Trying to run command: " + command);
        try {
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec(command2);
            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line=null;
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
    public ArrayList<File> findHTMLFilesInWorkingDirectory(String workingDirectory, String ID){
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

