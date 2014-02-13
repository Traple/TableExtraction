package program8;

import loft.program.Results;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;

//This is a class made for testing a single file.
public class Test {
    public static void main(String[] args) throws IOException, XPathExpressionException, SAXException, ParserConfigurationException {
        File file = new File("C:\\Users\\Sander van Boom\\Documents\\School\\tables\\matrixTest\\resources\\v10n1a03-15.html");
        Page page = new Page(file, "C:\\Users\\Sander van Boom\\Documents\\School\\tables\\matrixTest\\resources", false);
        page.createTables(2,1.2,4,3);
//        Results results = new Results("C:\\Users\\Sander van Boom\\Documents\\School\\tables\\TEA0.7EnzymeCorpus\\resources");
    }
}
