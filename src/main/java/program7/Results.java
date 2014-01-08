package program7;

import org.xml.sax.SAXException;

import javax.swing.text.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created for project: TableExtraction
 * In package: program7
 * Created with IntelliJ IDEA.
 * User: Sander van Boom
 * Date: 8-1-14
 * Time: 12:47
 */
public class Results {
    private ArrayList<String> headers;
    public Results(String workspace) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        this.headers = new ArrayList<String>();
        System.out.println("------------------------------RESULTS----------------------------------------");
//        workspace = workspace + ;
        ArrayList<String> XMLs = findXMLs(workspace + "/results");
        System.out.println(XMLs);
        findHeaders(XMLs);
        findSimilarHeaders();
    }

    public static ArrayList<String> findXMLs(String workspace){
        ArrayList<String> XMLFiles = new ArrayList<String>();
        File dir = new File(workspace);
        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".xml");
            }
        });
        for(File file : files){
            XMLFiles.add(file.getAbsolutePath());
        }
        return XMLFiles;
    }

    private void findHeaders(ArrayList<String> filepaths) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
        ArrayList<String> headers = new ArrayList<String>();
        for(String filepath : filepaths){
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder;
            org.w3c.dom.Document doc = null;
            builder = factory.newDocumentBuilder();
            doc = builder.parse(String.valueOf(filepath));

            // Create XPathFactory object
            XPathFactory xpathFactory = XPathFactory.newInstance();

            // Create XPath object
            XPath xpath = xpathFactory.newXPath();
            String name = null;

            XPathExpression expr =
            xpath.compile("/TEAFile/tableSemantics/headers");
            name = (String) expr.evaluate(doc, XPathConstants.STRING);
            name = name.replace("[", "");
            name = name.replace("]", "");
            String[] names = name.split(" ");
            ArrayList<String> namesList = new ArrayList<String>();
            for(int x = 0; x< names.length ; x++){
                if(!names[x].contains(",")){
                    namesList.add(names[x]);
                }
            }
            headers.addAll(namesList);

        }
        this.headers = headers;
    }
    private void findSimilarHeaders(){
        System.out.println(headers);
        for(String header : headers){
            if(headers.lastIndexOf(header) != headers.indexOf(header)){
                int occurrences = Collections.frequency(headers, header);
                System.out.println("header " + header + " occurs more then once!" + " (" + occurrences + ")");
            }

        }
    }
}

