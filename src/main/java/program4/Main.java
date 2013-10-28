package program4;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
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

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException, TransformerException {

        System.setProperty("java.util.logging.config.file", "/program4/log.properties");
        LogManager logMan=LogManager.getLogManager();
        logMan.readConfiguration(Main.class.getResourceAsStream("/program4/log.properties"));
        logMan.addLogger(LOGGER);

        LOGGER.info("Starting T.E.A. 0.4");
        LOGGER.info("Greetings user! My name is T.E.A., which stands for Table Extraction Algorithm. But if you want, you can call me Bob.");

        //TODO: Create the commandline arguments.
        //The following variables should be changed to commandline parameters in the future:
        //String fileLocation = "C:\\Users\\Sander van Boom\\Documents\\School\\tables\\OCR\\OCR\\36-3.html";

        String ID = "24089145";
        LOGGER.info("Used ID: " + ID);
        String resolution = "450";
        LOGGER.info("Used resolution: " + resolution);
        String workLocation = "C:/Users/Sander van Boom/Documents/School/tables/T.E.A. 0.4 test/";
        LOGGER.info("Used worklocation: : " + workLocation);
        REXArticleExtractor rex = new REXArticleExtractor(ID);
        String PDFLink = rex.getPDFLink();


        if(PDFLink == null){
            LOGGER.info("I'm really sorry but I can't find a PDF link to this pubmedID. I'm gonna skip this PubmedID.");
            System.exit(1);
        }
        LOGGER.info("I've found a PDF link for this PubmedID: " + PDFLink);
        PDFDownloader PDFDownloader = new PDFDownloader(PDFLink, ""+workLocation+ID+".pdf");

        ImageMagic ImageMagic = new ImageMagic(resolution, ("\"" + workLocation +ID+".pdf\""),"\""+workLocation+ID+".png\"" );
        ImageMagic.createBitmap();
        ArrayList<File> pngs = ImageMagic.findPNGFilesInWorkingDirectory(workLocation, ID);

        int x =0;
        for(File file : pngs){
            LOGGER.info("Hand me my equipment. I'm going to perform OCR on " + file.getName());
            Tesseract Tesseract = new Tesseract("\"" + workLocation + ID+"-"+x+".png\"","\""+workLocation+ID+"-"+x+"\"");
            Tesseract.runTesseract();
            x++;
        }
        Tesseract tesseract = new Tesseract("\"" + workLocation + ID+"-"+x+".png\"","\""+workLocation+ID+"-"+x+"\"");
        ArrayList<File> HTMLFiles = tesseract.findHTMLFilesInWorkingDirectory(workLocation, ID);
        for(File file : HTMLFiles){
            LOGGER.info("Searching for tables in: " + file.getName());
            Page page = new Page(file);
            page.createTables();
            LOGGER.info("------------------------------------------------------------------------------");

        }
        LOGGER.info("T.E.A. has run out of workable Bits, Bytes, whatever. Don't worry a new supply will come in next week!");
        LOGGER.info("T.E.A. is now entering sleep mode...");
    }

    public static void write(String filecontent, String location){
        FileWriter fileWriter = null;
        try {
            File newTextFile = new File(location);
            fileWriter = new FileWriter(newTextFile);
            fileWriter.write(filecontent);
            fileWriter.close();
        }
        catch (IOException ex) {
        }

    }
}