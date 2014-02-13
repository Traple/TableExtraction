package program8;

import org.apache.commons.cli.ParseException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Welcome to the code of T.E.A. 0.8.
 * @version 0.8
 * T.E.A. was made by Sander van Boom at the Birkbeck University in London. This was done with the help of Jan Czarnecki and Adrian Shepherd.
 * @author Sander van Boom
 * Development started at the 9th of September.
 * The current state of the software is development.
 *
 * What the code does:
 * You put some Pubmed ID's in your T.E.A. and T.E.A. extracts the PDF and uses ImageMagick (PDF -> bitmap converter) and
 * Tesseract (OCR software) to create an HTML of the words.
 * The rest of the code extracts the table and stores it in an XML file.
 */
public class Main {

    public static Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException, TransformerException, ParseException, InterruptedException {
        System.out.println("Starting T.E.A. 0.8");

        System.setProperty("java.util.logging.config.file", "/program6/log.properties");
        LogManager logMan=LogManager.getLogManager();
        logMan.readConfiguration(Main.class.getResourceAsStream("/program7/log.properties"));
        logMan.addLogger(LOGGER);

        LOGGER.info("Starting T.E.A. 0.8");
        LOGGER.info("Greetings user! My name is T.E.A., which stands for Table Extraction Algorithm.");
        System.out.println("Greetings user! My name is T.E.A., which stands for Table Extraction Algorithm.");

        //processing of the arguments:
        ArgumentProcessor arguments = new ArgumentProcessor(args);
        String workLocation = arguments.getWorkspace();
        System.out.println("The worklocation is: " + workLocation);

        //prepare the workspace so we have a separate place to store our output files.
        prepareWorkspace(workLocation, arguments.getDebugging());

        String pathToImageMagic = arguments.getPathToImageMagick();
        System.out.println("Path to Image Magic is: " + pathToImageMagic);
        String pathToTesseract = arguments.getPathToTesseract();
        System.out.println("And the path to Tesseract is: " + pathToTesseract);
        String pathToTesseractConfig = arguments.getPathToTesseractConfigFile();
        System.out.println("Which uses the following configuration file: " + pathToTesseractConfig);
        String resolution = "600";
        double horizontalThresholdModifier = arguments.getHorizontalThresholdModifier();
        double verticalThresholdModifier = arguments.getVerticalThresholdModifier();
        int allowedHeaderSize = arguments.getAllowedHeaderSize();
        int allowedHeaderIterations = arguments.getAllowedHeaderIterations();
        boolean debugging = arguments.getDebugging();
        boolean rotating = arguments.getRotateTables();

        if(arguments.getContainsPDFFiles()){
            LOGGER.info("Entering PDF mode");
            ArrayList<String> PDFFiles = ImageMagick.findPDFs(workLocation);
            for(String ID : PDFFiles){
                System.out.println("Path to Magic: " + pathToImageMagic);
                System.out.println("ID: " + ID);
                System.out.println("Worklocation: " + workLocation);
                System.out.println("Resolution: " + resolution);
                System.out.println("Debugging: " + debugging);
                LOGGER.info("Currently Processing: " + ID);
                secondMain(pathToImageMagic, workLocation, ID, resolution, pathToTesseract, pathToTesseractConfig, verticalThresholdModifier, horizontalThresholdModifier, debugging, rotating, allowedHeaderSize, allowedHeaderIterations);

            }
        }
        LOGGER.info("T.E.A. is now entering sleep mode...");
    }

    /**
     * This is the second method after the initial main call. This part of the code always runs, no matter what the current mode is.
     * The starting point of this method is the workspace with the PDF files.
     * @param pathToImageMagic pathToImageMagic The path to one of the dependencies: ImageMagick
     * @param workLocation WorkLocation The work location, containing the PDF
     * @param ID The name of the PDF with the extension.
     * @param resolution The resolution to be used by ImageMagick.
     * @param pathToTesseract The path to the second dependency : Tesseract.
     * @param pathToTesseractConfig The path to the configuration file to be used by Tesseract.
     * @param verticalThresholdModifier This is the vertical threshold used for deciding if cells are in a new line.
     * @param horizontalThresholdModifier This is the horizontal threshold used for deciding if cells are in a new column.
     * @param debugging A boolean stating if debugging mode is on or of.
     * @throws java.io.IOException
     */
    private static void secondMain(String pathToImageMagic, String workLocation, String ID, String resolution, String pathToTesseract, String pathToTesseractConfig, double verticalThresholdModifier, double horizontalThresholdModifier, boolean debugging, boolean rotating, int allowedHeaderSize, int allowedHeaderIterations) throws IOException {
        try{
            ImageMagick imagemagick = new ImageMagick(pathToImageMagic,workLocation, ID ,resolution);
            imagemagick.createPNGFiles();

            ArrayList<File> pngs = ImageMagick.findPNGFilesInWorkingDirectory(workLocation, ID);
            if(rotating){
                Rotate rotate = new Rotate(pathToImageMagic, pngs, workLocation);
                LOGGER.info("Now rotating the image");
                System.out.println("Rotating!");
                rotate.createRotatedImage();
                pngs = ImageMagick.findPNGFilesInWorkingDirectory(workLocation, ID);
            }
            if(!rotating){
                int x =0;
                for(File file : pngs){
                    LOGGER.info("Hand me my equipment. I'm going to perform OCR on " + file.getName());
                Tesseract tesseract = new Tesseract(pathToTesseract, workLocation, ID, Integer.toString(x),pathToTesseractConfig);
                tesseract.runTesseract();
                x++;
                }
            }
            else {
                System.out.println("Rotating.");
                for(File file : pngs){
                    LOGGER.info("Hand me my equipment. I'm going to perform OCR on " + file.getName());
                    Tesseract tesseract = new Tesseract(pathToTesseract, file, workLocation, pathToTesseractConfig);
                    tesseract.runTesseract();
                }
            }
            System.out.println("find HTML files: ");
            ArrayList<File> HTMLFiles = Tesseract.findHTMLFilesInWorkingDirectory(workLocation, ID);
            for(File file : HTMLFiles){
                LOGGER.info("Searching for tables in: " + file.getName());
                System.out.println("Searching for tables in: " + file.getName());
                Page page = new Page(file, workLocation, debugging);
                System.out.println("The found average length of a character is: " + page.getSpaceDistance());
                page.createTables(horizontalThresholdModifier, verticalThresholdModifier, allowedHeaderSize, allowedHeaderIterations);
                LOGGER.info("------------------------------------------------------------------------------");
                System.out.println("------------------------------------------------------------------------------");
            }
        }
        catch (IOException e){
            System.out.println(e);
            LOGGER.info(e.toString());
        }
    }

    /**
     * This method prepares the workspace by creating the necessary directories.
     * Currently it only creates the results directory in the workspace to store the results.
     */
    private static void prepareWorkspace(String workspace, boolean debugging){
        File file = new File(workspace + "/results");

        if (file.mkdir())
        {
            System.out.println("Directory = " + file.getAbsolutePath() + " was created.");
        } else
        {
            System.out.println("No directory was created.");
        }
        if(debugging)   {
            File file2 = new File(workspace + "/debuggingFiles");
            file2.mkdir();
        }
    }
    }
