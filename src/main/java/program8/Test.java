package program8;

import loft.program.Results;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

//This is a class made for testing a single file.
public class Test {
    public static void main(String[] args) throws IOException, XPathExpressionException, SAXException, ParserConfigurationException {
//        File file = new File("C:\\Users\\Sander van Boom\\Documents\\School\\tables\\TEA0.7EnzymeCorpus\\resources\\20-2.html.html");
//        Page page = new Page(file, "C:\\Users\\Sander van Boom\\Documents\\School\\tables\\TEA0.7EnzymeCorpus\\resources", false);
//        page.createTables(2,1.2);
        Results results = new Results("C:\\Users\\Sander van Boom\\Documents\\School\\tables\\TEA0.7EnzymeCorpus\\resources");
    }
}
