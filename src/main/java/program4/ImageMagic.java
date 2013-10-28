package program4;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Logger;

public class ImageMagic {
    private String command;
    public static Logger LOGGER = Logger.getLogger(ImageMagic.class.getName());
    //convert -density 600 -units PixelsPerInch input.pdf output.png

    public ImageMagic(String resolution, String fileLocation, String outputLocation){
        String path = "\"C:\\Program Files (x86)\\ImageMagick-6.8.7-Q16\\convert.exe\"";
        this.command = path + " -density " + resolution + " -units PixelsPerInch " + fileLocation + " " + outputLocation;
    }

    public void createBitmap(){
        LOGGER.info("Trying to run command: " + command);
        System.out.println("Trying to run command: " + command);
        try {
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec(command);
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
    public ArrayList<File> findPNGFilesInWorkingDirectory(String workingDirectory, String ID){
        ArrayList<File> pngFiles = new ArrayList<File>();
        File dir = new File(workingDirectory);
        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".png");
            }
        });
        for(File file : files){
            if(file.getName().startsWith(ID)){
                System.out.println(file.getAbsolutePath());
                pngFiles.add(file);
            }
        }
        return pngFiles;
    }

    public void deletePNGFiles(){

    }
}
