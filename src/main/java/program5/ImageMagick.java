package program5;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * This class calls one of the dependencies ImageMagick.
 * there are four parameters: Path to ImageMagick, the workspace, the ID of the pdf file and the resolution used by ImageMagick
 *
 * Example:
 * imageMagick("/usr/bin/convert", /usr/bin/MyWorkspace, quantumPhysics, 600);
 *
 * Please note that workspace should be without a /, the program adds this.
 * Also don't give it the extension with the ID, just the name.
 *
 * For clearification see lines:
 * String output = workspace+"/" + ID + ".png";
 * String input = workspace +"/"+ ID + ".pdf";
 *
 */
public class ImageMagick {

    private String[] command;
    private String workspace;
    public static Logger LOGGER = Logger.getLogger(ImageMagick.class.getName());

    public ImageMagick(String path, String workspace, String ID, String resolution){
        this.workspace = workspace + "/";
        String output = workspace+"/" + ID + ".png";
        String input = workspace +"/"+ ID + ".pdf";
        String[] command = {path, "-density",resolution,"-units","PixelsPerInch",input, output};
        this.command = command;
    }

    /**
     * this method was re-written in the conversion between TEA 0.4 and 0.5. This was required for supporting Linux.
     * The script runs the command on the commandline.
     * @throws java.io.IOException
     */
    public void createPNGFiles() throws IOException {
        System.out.println("Trying to run command: "+ Arrays.toString(command));
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

    /**
     * This method will find all the pdf files that correspond with an ID of the PDF.
     * @param workingDirectory the workingDirectory is the directory that is being screened for pdf files.
     * @param ID the ID of the pdf file.
     * @return A list of pdf files that correpsond with this ID (in other words, the pages)
     */
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
    //TODO: Create checkPDF method so we can have different starting points in our software.
}
