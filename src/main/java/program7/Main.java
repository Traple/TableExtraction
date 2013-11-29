package program7;

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
 * Welcome to the code of T.E.A. 0.6.
 * @version 0.6
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
        System.out.println("Starting T.E.A. 0.6");

        System.setProperty("java.util.logging.config.file", "/program6/log.properties");
        LogManager logMan=LogManager.getLogManager();
        logMan.readConfiguration(Main.class.getResourceAsStream("/program7/log.properties"));
        logMan.addLogger(LOGGER);

        LOGGER.info("Starting T.E.A. 0.6");
        LOGGER.info("Greetings user! My name is T.E.A., which stands for Table2 Extraction Algorithm. But if you want, you can call me Bob.");
        System.out.println("Greetings user! My name is T.E.A., which stands for Table2 Extraction Algorithm. But if you want, you can call me Bob.");

        //processing of the arguments:
        ArgumentProcessor arguments = new ArgumentProcessor(args);
        String workLocation = arguments.getWorkspace();
        System.out.println("The worklocation is: " + workLocation);
        ArrayList<String> pubmedIDs = arguments.getPubmedIDs();

        //prepare the workspace so we have a separate place to store our output files.
        prepareWorkspace(workLocation);

        String pathToImageMagic = arguments.getPathToImageMagick();
        System.out.println("Path to Image Magic is: " + pathToImageMagic);
        String pathToTesseract = arguments.getPathToTesseract();
        System.out.println("And the path to Tesseract is: " + pathToTesseract);
        String pathToTesseractConfig = arguments.getPathToTesseractConfigFile();
        System.out.println("Which uses the following configuration file: " + pathToTesseractConfig);
        String resolution = "600";
        double horizontalThresholdModifier = arguments.getHorizontalThresholdModifier();
        double verticalThresholdModifier = arguments.getVerticalThresholdModifier();
        long start = System.currentTimeMillis();
        /*
        if(pubmedIDs == null){
            System.out.println("Pubmed File option disabled.");
        }
        else{
            String ID = "";
            for(String pubmedID : pubmedIDs){
                ID = pubmedID;


                System.out.println("Currently processing: " + ID);
                LOGGER.info("Used ID: " + ID);

                REXArticleExtractor3 rex = new REXArticleExtractor3(ID);
                if(rex.hasPDFStream()){
                    InputStream stream = rex.getPDFStream();
                    OutputStream  outStream = new BufferedOutputStream(new FileOutputStream(new File(workLocation+"/"+ID+".pdf")));
                    IOUtils.copy(stream, outStream);

                    System.out.println("Path to Magic: " + pathToImageMagic);
                    System.out.println("ID: " + ID);
                    System.out.println("Worklocation: " + workLocation);
                    System.out.println("Resolution: " + resolution);
                    LOGGER.info("");
                    secondMain(pathToImageMagic, workLocation, ID, resolution,pathToTesseract, pathToTesseractConfig);
                }
            }
        }      */
        if(arguments.getContainsPDFFiles()){
            LOGGER.info("Entering PDF mode");
            ArrayList<String> PDFFiles = ImageMagick.findPDFs(workLocation);
            for(String ID : PDFFiles){
                System.out.println("Path to Magic: " + pathToImageMagic);
                System.out.println("ID: " + ID);
                System.out.println("Worklocation: " + workLocation);
                System.out.println("Resolution: " + resolution);
                LOGGER.info("Currently Processing: " + ID);
                secondMain(pathToImageMagic, workLocation, ID, resolution, pathToTesseract, pathToTesseractConfig, verticalThresholdModifier, horizontalThresholdModifier);
            }
        }   /*
        if(arguments.getQuery()!=null){
            LOGGER.info("Entering Query mode");
            String numberOfArticles = "1000";
            System.out.println("Im gonna extract " + numberOfArticles+".");
            LOGGER.info("I'm going to extract " + numberOfArticles + " articles.");
            PubmedIDQuery pubmedIDQuery = new PubmedIDQuery(arguments.getQuery(), arguments.getWorkspace(), numberOfArticles);
            ArrayList<String> pubmedIDsFromQuery = pubmedIDQuery.getPubmedIDs();
            System.out.println("Extracted " +pubmedIDsFromQuery.size() + " articles");
            LOGGER.info("Extracted " +pubmedIDsFromQuery.size() + " articles");
            for(String ID : pubmedIDsFromQuery){
                System.out.println("processing ID: "+ID);
                System.out.println("Time spend: ");
                System.out.println((System.currentTimeMillis()-start)/1000);
                if((System.currentTimeMillis()-start)/1000<5){
                    long waitTime = 5-(System.currentTimeMillis()-start)/1000;
                    System.out.println("I have to wait :" + waitTime + " seconds");
                    Thread.sleep(waitTime);
                }
                try{
                REXArticleExtractor3 rex = new REXArticleExtractor3(ID);
                if(rex.hasPDFStream()){
                    InputStream stream = rex.getPDFStream();
                    PDDocument doc = PDDocument.load(stream);
                    if(doc.getNumberOfPages() > 40){
                        System.out.println("This document has been removed for having to many pages.");
                        LOGGER.info("This document has " + doc.getNumberOfPages() + " pages. I think I have something better to do!");
                        doc.close();
                        continue;
                    }
                    doc.close();
                    InputStream inputStream = rex.getPDFStream();
                    OutputStream  outStream = new BufferedOutputStream(new FileOutputStream(new File(workLocation+"/"+ID+".pdf")));
                    IOUtils.copy(inputStream, outStream);
                    start = System.currentTimeMillis();
                    outStream.close();
                    inputStream.close();

                    secondMain(pathToImageMagic, workLocation,ID, resolution, pathToTesseract, pathToTesseractConfig);
                    }
                    else{
                    LOGGER.info("There was no PDF Stream.");
                }
                }
                    catch(SocketTimeoutException e){
                    System.out.println(e);
                }
                catch (HttpStatusException e){
                    System.out.println(e);
                }
                catch (UnsupportedMimeTypeException e){
                    System.out.println(e);
                }
            }
        }  */
        LOGGER.info("T.E.A. is now entering sleep mode...");
    }

    /**
     * This method is the second. This part of the code is always run, no matter what the current mode is.
     * The starting point of this method is the workspace with the PDF files.
     * @param pathToImageMagic The path to one of the dependencies: ImageMagick
     * @param workLocation The work location, containing the PDF
     * @param ID The name of the PDF with the extension.
     * @param resolution The resolution to be used by ImageMagick.
     * @param pathToTesseract The path to the second dependency : Tesseract.
     * @param pathToTesseractConfig The path to the configuration file to be used by Tesseract.
     * @throws java.io.IOException
     */
    private static void secondMain(String pathToImageMagic, String workLocation, String ID, String resolution, String pathToTesseract, String pathToTesseractConfig, double verticalThresholdModifier, double horizontalThresholdModifier) throws IOException {
       /*
        ImageMagick imagemagick = new ImageMagick(pathToImageMagic,workLocation, ID ,resolution);
        imagemagick.createPNGFiles();

        ArrayList<File> pngs = ImageMagick.findPNGFilesInWorkingDirectory(workLocation, ID);

        int x =0;
        for(File file : pngs){
            LOGGER.info("Hand me my equipment. I'm going to perform OCR on " + file.getName());
            Tesseract Tesseract = new Tesseract(pathToTesseract, workLocation, ID, Integer.toString(x),pathToTesseractConfig);
            Tesseract.runTesseract();
            x++;
        }
         */
        ArrayList<File> HTMLFiles = Tesseract.findHTMLFilesInWorkingDirectory(workLocation, ID);
        for(File file : HTMLFiles){
            LOGGER.info("Searching for tables in: " + file.getName());
            System.out.println("Searching for tables in: " + file.getName());
            Page page = new Page(file, workLocation);
            System.out.println("The found average length of a character is: " + page.getSpaceDistance());
            page.createTables(horizontalThresholdModifier, verticalThresholdModifier);
            LOGGER.info("------------------------------------------------------------------------------");
            System.out.println("------------------------------------------------------------------------------");
        }
    }

    /**
     * This method prepares the workspace by creating the necessary directories.
     * Currently it only creates the results directory in the workspace to store the results.
     */
    private static void prepareWorkspace(String workspace){
        File file = new File(workspace + "/results");

        if (file.mkdir())
        {
            System.out.println("Directory = " + file.getAbsolutePath() + " was created.");
        } else
        {
            System.out.println("No directory was created.");
        }
    }
}