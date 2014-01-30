package loft.program;

import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


//Currently not being used. Replaced to the semantic part of TEA.
public class Results {
    private ArrayList<String> headers;
    private ArrayList<String> uniqueHeaders;

    public Results(String workspace) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        this.headers = new ArrayList<String>();
        System.out.println("------------------------------RESULTS----------------------------------------");
        ArrayList<String> XMLs = findXMLs(workspace + "/results");
        System.out.println(XMLs);
        findHeaders(XMLs);
        findSimilarHeaders();
        findUniqueHeaderValues(XMLs);
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
            org.w3c.dom.Document doc;
            builder = factory.newDocumentBuilder();
            doc = builder.parse(String.valueOf(filepath));

            // Create XPathFactory object
            XPathFactory xpathFactory = XPathFactory.newInstance();

            // Create XPath object
            XPath xpath = xpathFactory.newXPath();
            String name;

            XPathExpression expr =
            xpath.compile("/TEAFile/tableSemantics/headers");
            name = (String) expr.evaluate(doc, XPathConstants.STRING);
            name = name.replace("[", "");
            name = name.replace("]", "");
            String[] names = name.split(" ");
            ArrayList<String> namesList = new ArrayList<String>();
            for (String name1 : names) {
                if (!name1.contains(",")) {
                    namesList.add(name1);
                }
            }
            headers.addAll(namesList);

        }
        this.headers = headers;
    }
    private void findSimilarHeaders(){
        System.out.println(headers);
        ArrayList<String> uniqueHeaders = new ArrayList<String>();
        for(String header : headers){
            if(headers.lastIndexOf(header) != headers.indexOf(header)){
                int occurrences = Collections.frequency(headers, header);
                if(!uniqueHeaders.contains(header)){
                    uniqueHeaders.add(header);
                }
                System.out.println("header " + header + " occurs more then once!" + " (" + occurrences + ")");
            }
        }
        this.uniqueHeaders = uniqueHeaders;
    }
    private void findUniqueHeaderValues(ArrayList<String> filepaths) throws XPathExpressionException, ParserConfigurationException, IOException, SAXException {
        for(String header : uniqueHeaders){
            System.out.println(header + " contains the following results: ");
            for(String filepath : filepaths){
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setNamespaceAware(true);
                DocumentBuilder builder;
                org.w3c.dom.Document doc;
                builder = factory.newDocumentBuilder();
                doc = builder.parse(String.valueOf(filepath));

                // Create XPathFactory object
                XPathFactory xpathFactory = XPathFactory.newInstance();

                // Create XPath object
                XPath xpath = xpathFactory.newXPath();
                String name;

                XPathExpression expr =
                        xpath.compile("/TEAFile/tableSemantics/headers");
                name = (String) expr.evaluate(doc, XPathConstants.STRING);
                name = name.replace("[", "");
                name = name.replace("]", "");
                String[] names = name.split(" ");

                ArrayList<String> namesList = new ArrayList<String>();
                for (String name1 : names) {
                    if (!name1.contains(",")) {
                        namesList.add(name1);
                    }
                }
                String results;
                if(namesList.contains(header)){
                    System.out.println(namesList);

                    XPathExpression expr2 =
                            xpath.compile("/TEAFile/results/columns");
                    results = (String) expr2.evaluate(doc, XPathConstants.STRING);
                    System.out.println(results);
                }
            }

        }
    }
}

