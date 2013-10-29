package program4;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;
import org.im4java.core.ConvertCmd;

public class ImageMagic {
    private String[] command;
    public static Logger LOGGER = Logger.getLogger(ImageMagic.class.getName());
    //convert -density 600 -units PixelsPerInch input.pdf output.png

    public ImageMagic(String workspace, String path, String ID, String resolution){
        //Windows:
        //String path = "\"C:\\Program Files (x86)\\ImageMagick-6.8.7-Q16\\convert.exe\"";
        //Linux:
        //String path = "/usr/bin/convert";
        //String[] command2 = {path, "-density", resolution, "-units","PixelsPerInch", fileLocation, outputLocation};
        //String[] command2 = {path, "/d/usr5/ubcg60f/TEA0.4/firsttest/24089145.pdf", "/d/usr5/ubcg60f/TEA0.4/firsttest/24089145.png"};
        String output = workspace + ID + ".png";
        String input = workspace + ID + ".pdf";
        String[] command = {path, "-density",resolution,"-units","PixelsPerInch",input, output};
        this.command = command;
        //this.command = path + " -density " + resolution + " -units PixelsPerInch " + fileLocation + " " + outputLocation;
    }

    /**
     * this method is going to work
     * @throws IOException
     */
    public void createPNGFiles() throws IOException {
        System.out.println(command);
        ProcessBuilder probuilder = new ProcessBuilder(command);
        //You can set up your work directory
        probuilder.directory(new File("/d/user5/ubcg60f/TEA0.4/firsttest"));

        Process process = probuilder.start();

        //Read out dir output
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
                    /*
    public void createBitmap(){
        LOGGER.info("Trying to run command: " + command2);
        System.out.println("Trying to run command: " + command2[0] + " " +command2[1]+ " "+ command2[2] );
        try {
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec(command2);
            System.out.println(pr.getErrorStream().read());
            System.out.println(pr.getInputStream().read());
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

    public void test(String[] testCommand) throws IOException {
        System.out.println(testCommand[0] + " " +testCommand[1] + " "+ testCommand[2]);
        ProcessBuilder probuilder = new ProcessBuilder(testCommand);
        //You can set up your work directory
        probuilder.directory(new File("/d/user5/ubcg60f/TEA0.4/firsttest"));

        Process process = probuilder.start();

        //Read out dir output
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;
        System.out.printf("Output of running %s is:\n",
                Arrays.toString(testCommand));
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }

        //Wait to get exit value
        try {
            int exitValue = process.waitFor();
            System.out.println("\n\nExit Value is " + exitValue);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("ANOTHER TIME:");

        System.out.println(command[0] + " " +command[1] + " "+ command[2]);
        probuilder = new ProcessBuilder(testCommand);
        //You can set up your work directory
        probuilder.directory(new File("/d/user5/ubcg60f/TEA0.4/firsttest"));

        process = probuilder.start();

        //Read out dir output
        is = process.getInputStream();
        isr = new InputStreamReader(is);
        br = new BufferedReader(isr);
        System.out.printf("Output of running %s is:\n",
                Arrays.toString(testCommand));
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

    public void deletePNGFiles(){

    }
}
