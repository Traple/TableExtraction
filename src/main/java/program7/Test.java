package program7;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException, XPathExpressionException, SAXException, ParserConfigurationException {
//        File file = new File("C:\\Users\\Sander van Boom\\Documents\\School\\tables\\TEA0.7UnileverTest\\resources\\33331_ftp-10.html");
//        Page page = new Page(file, "C:\\Users\\Sander van Boom\\Documents\\School\\tables\\TEA0.7UnileverTest\\resources", false);
//        page.createTables(2,1.2);
        Results results = new Results("C:\\Users\\Sander van Boom\\Documents\\School\\tables\\TEA0.7EnzymeCorpus\\resources");
    }
}
