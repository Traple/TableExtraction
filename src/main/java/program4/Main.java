package program4;

import org.apache.commons.cli.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Welcome to the code of T.E.A. 0.4.
 * T.E.A. was made by Sander van Boom at the Birkbeck University in London. This was done with the help of Jan Czarnecki and Adrian Shepherd.
 * Development started at the 9th of September.
 * The current state of the software is development.
 *
 * What the code does:
 * You put some Pubmed ID's in your T.E.A. and T.E.A. extracts the PDF and uses ImageMagic (PDF -> bitmap converter) and
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

        LOGGER.info("Starting T.E.A. 0.4");
        LOGGER.info("Greetings user! My name is T.E.A., which stands for Table Extraction Algorithm. But if you want, you can call me Bob.");

        CommandLineParser parser = new PosixParser();
        Options options = new Options();

        Option help = new Option("H", "Help",false ,"This is the help file of TEA.");
        Option optionPubmedIDs = new Option("P", "pubmedIDFile", true, "The file with the PubmedID's");
        Option optionWorkspace = new Option("W", "workspace", true, "The workspace for the program.");

        options.addOption(optionPubmedIDs);
        options.addOption(optionWorkspace);

        CommandLine line = parser.parse(options, args);
        String pubmedFile = "";
        String workspaceArg = "";
        if (line.hasOption("P")&&line.hasOption("W")){
            pubmedFile = line.getOptionValue("P");
            workspaceArg = line.getOptionValue("W");
        }
        else{
            System.exit(1);
        }
        //TODO: replace the hardcoded paths to the dependencies to a config file.
        System.out.println("From the arguments I got: " + pubmedFile);
        System.out.println("And :" + workspaceArg);
        String pathToImageMagic = "/usr/bin/convert";
        System.out.println("Path to Image Magic is hardcoded to: " + pathToImageMagic);
        String pathToTesseract = "/d/as2/s/tesseract-ocr/bin/tesseract";
        System.out.println("And the path to Tesseract is hardcoded to: " + pathToTesseract);
        String pathToTesseractConfig = "/d/user5/ubcg60f/TEA0.4/config.txt";

        BufferedReader br = new BufferedReader(new FileReader(pubmedFile));
        String readline;
        ArrayList<String> pubmedIDs = new ArrayList<String>();
        while ((readline = br.readLine()) != null) {
            pubmedIDs.add(readline);
        }
        br.close();

        //TODO: Fix this loop!
        String ID = "";
        for(String pubmedID : pubmedIDs){
            ID = pubmedID;
        }

        System.out.println("Currently processing: " + ID);
        LOGGER.info("Used ID: " + ID);
        String resolution = "450";
        LOGGER.info("Used resolution: " + resolution);
        String workLocation = workspaceArg;
        //String workLocation = "C:/Users/Sander van Boom/Documents/School/tables/T.E.A. 0.4 test/";
        LOGGER.info("Used worklocation: : " + workLocation);

        REXArticleExtractor rex = new REXArticleExtractor(ID);
        String PDFLink = rex.getPDFLink();

        if(PDFLink == null){
            LOGGER.info("I'm really sorry but I can't find a PDF link to this pubmedID. I'm gonna skip this PubmedID.");
            System.exit(1);
        }
        LOGGER.info("I've found a PDF link for this PubmedID: " + PDFLink);
        PDFDownloader PDFDownloader = new PDFDownloader(PDFLink, ""+workLocation+ID+".pdf");

        System.out.println("Path to Magic: " + pathToImageMagic);
        System.out.println("ID: " + ID);
        System.out.println("Worklocation: " + workLocation);
        System.out.println("Resolution: " + resolution);

        ImageMagic imagemagic = new ImageMagic(pathToImageMagic,workLocation, ID ,resolution);
        imagemagic.createPNGFiles();

        ArrayList<File> pngs = imagemagic.findPNGFilesInWorkingDirectory(workLocation, ID);

        int x =0;
        for(File file : pngs){
            LOGGER.info("Hand me my equipment. I'm going to perform OCR on " + file.getName());
            Tesseract Tesseract = new Tesseract(pathToTesseract, workLocation, ID, Integer.toString(x),pathToTesseractConfig);
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

        LOGGER.info("T.E.A. has run out of workable Bits, Bytes, whatever. Don't worry a new supply will come in next week!");
        LOGGER.info("T.E.A. is now entering sleep mode...");
    }
}