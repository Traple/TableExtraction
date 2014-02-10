package program8;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

//The rotate class will call the dependency ImageMagick to rotate the image.
//This requires:
// - Rotate function in Argument processor
// - Create Auto rotate option (detection of vertical tables).
// - Create extra .html's in the list of html's in the Main class.
public class Rotate {
    private String workspace;

    //convert -rotate -45 a.png b.png

    public ArrayList<File> PNGFiles;
    public String [] command;
    public static Logger LOGGER = Logger.getLogger(Rotate.class.getName());

    public Rotate(String path, ArrayList<File> PNGFiles, String workspace) throws IOException {
        this.PNGFiles = PNGFiles;
        for(File file: PNGFiles){
            int counter = 0;
            int degrees = -90;
            System.out.println("Now rotating: "+file.getAbsolutePath());
            while(counter<2){
                System.out.println("\""+file.getAbsolutePath().substring(0, file.getAbsolutePath().length()-4)+"-"+counter+".png\"");
                degrees += 180;
                String outputFile = "\""+file.getAbsolutePath().substring(0, file.getAbsolutePath().length()-4)+"-"+counter+".png\"";
                this.command = new String[] {path, "-rotate", "-"+degrees, "\""+file.getAbsolutePath()+"\"", outputFile};
                this.workspace = workspace;
                counter++;
            }
        }
    }
    public void createRotatedImage() throws IOException {
        System.out.println("Trying to run command: " + Arrays.toString(command));
        LOGGER.info("Trying to run command: " + Arrays.toString(command));

        ProcessBuilder probuilder = new ProcessBuilder(command);
        probuilder.directory(new File(workspace));

        Process process = probuilder.start();

        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;
        System.out.printf("Output of running %s is:\n",
                Arrays.toString(command));
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }

        //Wait to get exit value
        try {
            int exitValue = process.waitFor();
            System.out.println("\n\nExit Value is " + exitValue);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
