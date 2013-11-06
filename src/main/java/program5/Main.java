package program5;

import org.apache.commons.cli.*;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.jsoup.HttpStatusException;
import org.jsoup.UnsupportedMimeTypeException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.*;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Welcome to the code of T.E.A. 0.5.
 * @version 0.5
 * T.E.A. was made by Sander van Boom at the Birkbeck University in London. This was done with the help of Jan Czarnecki and Adrian Shepherd.
 * @author Sander van Boom
 * Development started at the 9th of September.
 * The current state of the software is development.
 *
 * What the code does:
 * You put some Pubmed ID's in your T.E.A. and T.E.A. extracts the PDF and uses ImageMagick (PDF -> bitmap converter) and
 * Tesseract (OCR software) to create an HTML of the words.
 * The rest of the code extracts the table and stores it in an XML file.
 *
 * Note that none of the used code/software has 100% accuracy.
 */
public class Main {

    public static Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException, TransformerException, ParseException, InterruptedException {
        System.out.println("Starting T.E.A. 0.4");


        System.setProperty("java.util.logging.config.file", "/program4/log.properties");
        LogManager logMan=LogManager.getLogManager();
        logMan.readConfiguration(Main.class.getResourceAsStream("/program4/log.properties"));
        logMan.addLogger(LOGGER);

        LOGGER.info("Starting T.E.A. 0.5");
        LOGGER.info("Greetings user! My name is T.E.A., which stands for Table Extraction Algorithm. But if you want, you can call me Bob.");
        System.out.println("Greetings user! My name is T.E.A., which stands for Table Extraction Algorithm. But if you want, you can call me Bob.");

        ArgumentProcessor arguments = new ArgumentProcessor(args);
        String workLocation = arguments.getWorkspace();
        ArrayList<String> pubmedIDs = arguments.getPubmedIDs();

        String pathToImageMagic = arguments.getPathToImageMagick();
        System.out.println("Path to Image Magic is: " + pathToImageMagic);
        String pathToTesseract = arguments.getPathToTesseract();
        System.out.println("And the path to Tesseract is: " + pathToTesseract);
        String pathToTesseractConfig = arguments.getPathToTesseractConfigFile();
        System.out.println("Which uses the following configuration file: " + pathToTesseractConfig);
        String resolution = "600";
        long start = System.currentTimeMillis();

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
        }
        //for logging purpose:
        String PDFLink = rex.getPDFLink();
                /*
        if(PDFLink == null){
            LOGGER.info("I'm really sorry but I can't find a PDF link to this pubmedID. I'm gonna skip this PubmedID.");
            continue;
        }
            //TODO: rewrite the PDF downloader so it uses more then just the constructor.
            LOGGER.info("I've found a PDF link for this PubmedID: " + PDFLink);
            PDFDownloader PDFDownloader = new program5.PDFDownloader(PDFLink, ""+workLocation+"/"+ID+".pdf");
                  */
            System.out.println("Path to Magic: " + pathToImageMagic);
            System.out.println("ID: " + ID);
            System.out.println("Worklocation: " + workLocation);
            System.out.println("Resolution: " + resolution);
            secondMain(pathToImageMagic, workLocation, ID, resolution,pathToTesseract, pathToTesseractConfig);
            }
        }
        if(arguments.getContainsPDFFiles()){
            ArrayList<String> PDFFiles = ImageMagick.findPDFs(workLocation);
            for(String ID : PDFFiles){
                System.out.println("Path to Magic: " + pathToImageMagic);
                System.out.println("ID: " + ID);
                System.out.println("Worklocation: " + workLocation);
                System.out.println("Resolution: " + resolution);
                LOGGER.info("Currently Processing: " + ID);
                secondMain(pathToImageMagic, workLocation, ID, resolution, pathToTesseract, pathToTesseractConfig);
            }
        }
        if(arguments.getQuery()!=null){
            String numberOfArticles = "1000";
            System.out.println("Im gonna extract " + numberOfArticles+".");
            LOGGER.info("I'm going to extract " + numberOfArticles + " articles.");
            PubmedIDQuery pubmedIDQuery = new PubmedIDQuery(arguments.getQuery(), workLocation, numberOfArticles);
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
                }
                secondMain(pathToImageMagic, workLocation,ID, resolution, pathToTesseract, pathToTesseractConfig);
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
            }

        LOGGER.info("T.E.A. has run out of workable Bits, Bytes, whatever. Don't worry a new supply will come in next week!");
        LOGGER.info("T.E.A. is now entering sleep mode...");
    }

    private static void secondMain(String pathToImageMagic, String workLocation, String ID, String resolution, String pathToTesseract, String pathToTesseractConfig) throws IOException {
        ImageMagick imagemagick = new ImageMagick(pathToImageMagic,workLocation, ID ,resolution);
        imagemagick.createPNGFiles();

        ArrayList<File> pngs = imagemagick.findPNGFilesInWorkingDirectory(workLocation, ID);

        int x =0;
        for(File file : pngs){
            LOGGER.info("Hand me my equipment. I'm going to perform OCR on " + file.getName());
            Tesseract Tesseract = new program5.Tesseract(pathToTesseract, workLocation, ID, Integer.toString(x),pathToTesseractConfig);
            Tesseract.runTesseract();
            x++;
        }

        Tesseract tesseract = new Tesseract(pathToTesseract, workLocation, ID, Integer.toString(x),pathToTesseractConfig);
        ArrayList<File> HTMLFiles = tesseract.findHTMLFilesInWorkingDirectory(workLocation, ID);
        for(File file : HTMLFiles){
            LOGGER.info("Searching for tables in: " + file.getName());
            System.out.println("Searching for tables in: " + file.getName());
            Page page = new Page(file, workLocation);
            page.createTables();
            LOGGER.info("------------------------------------------------------------------------------");
            System.out.println("------------------------------------------------------------------------------");
        }
    }

    private static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        return dateFormat.format(date);
    }
}