package program4;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

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
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException, TransformerException {

        //TODO: Create the commandline arguments.
        //The following variables should be changed to commandline parameters in the future:
        String fileLocation = "C:\\Users\\Sander van Boom\\Documents\\School\\tables\\OCR\\OCR\\36-3.html";

        REXArticleExtractor rex = new REXArticleExtractor("24085914");

        PDFDownloader PDFDownloader = new PDFDownloader(rex.getPDFLink(), "C:\\Users\\Sander van Boom\\Documents\\School\\tables\\T.E.A. 0.4 test");

        //ImageMagic ImageMagic = new ImageMagic();

        //Tesseract Tesseract = new Tesseract();

        //Page page = new Page(fileLocation);
        //page.createTables();

    }
}
