package program6;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

public class PageTest {
    private Elements spans;
    @Before
    public void PageTest() throws IOException {
        File file = new File("./src/test/resources/program6/48-3.html.html");
        Document doc = Jsoup.parse(file, "UTF-8", "http://example.com/");
        this.spans = doc.select("span.ocrx_word");
    }
    /**
     * This test is made of the original createTables method in the Page class to check if the
     */
    @Test
    public void createTablesTest1() throws IOException {
        String word;
        boolean foundATable = false;
        Elements tableSpans = new Elements();
        ArrayList<Element> foundTables = new ArrayList<Element>();
        for (Element span : spans) {
            word = span.text();
            try {
                if(word.substring(0, 5).equals("TABLE") || word.substring(0, 5).equals("table") || word.substring(0, 5).equals("Table")&&foundATable){
                    foundTables.add(tableSpans.get(0)); //make a new table from the collected spans
                    tableSpans = new Elements();        //reset the spans for the new Table
                    tableSpans.add(span);
                }
                else if (foundATable) {
                    tableSpans.add(span);
                }
                if (word.substring(0, 5).equals("TABLE") || word.substring(0, 5).equals("table") || word.substring(0, 5).equals("Table")&&!foundATable) {
                    foundATable = true;
                    tableSpans.add(span);
                }
            } catch (StringIndexOutOfBoundsException e) {
                if(foundATable){
                    tableSpans.add(span);
                }
            }
        }
        if (foundATable) {
            foundTables.add(tableSpans.get(0));
        }
        if(!foundATable){
            //LOGGER.info("There was no table found. ");
            System.out.println("No table found =(");
        }
        assertEquals("[<span class=\"ocrx_word\" id=\"word_6\" title=\"bbox 1041 612 1201 658\"><strong>Table</strong></span>, <span class=\"ocrx_word\" id=\"word_541\" title=\"bbox 2575 3207 2762 3265\">Table</span>]", foundTables.toString());
        System.out.println("Table creation matches.");
    }


    /**
     * This test actually tests if the table objects are being created.
     * We check this by getting the names of the table detections (in the used example, 1 is true positive, other is false positive).
     * @throws IOException
     */
    @Test
    public void createTablesTest2() throws IOException {
        String word;
        boolean foundATable = false;
        Elements tableSpans = new Elements();
        ArrayList<Table2> foundTables = new ArrayList<Table2>();
        for (Element span : spans) {
            word = span.text();

            try {
                if(word.substring(0, 5).equals("TABLE") || word.substring(0, 5).equals("table") || word.substring(0, 5).equals("Table")&&foundATable){
                    //foundTables.add(new Table2(tableSpans, 35.874534161490686)); //make a new table from the collected spans
                    tableSpans = new Elements();        //reset the spans for the new Table
                    tableSpans.add(span);
                }
                else if (foundATable) {
                    tableSpans.add(span);
                }
                if (word.substring(0, 5).equals("TABLE") || word.substring(0, 5).equals("table") || word.substring(0, 5).equals("Table")&&!foundATable) {
                    foundATable = true;
                    tableSpans.add(span);
                }
            } catch (StringIndexOutOfBoundsException e) {
                if(foundATable){
                    tableSpans.add(span);
                }
            }
        }
        if (foundATable) {
            //foundTables.add(new Table2(tableSpans, 35.874534161490686));
        }
        if(!foundATable){
            //LOGGER.info("There was no table found. ");
            System.out.println("No table found =(");
        }
        assertEquals("Table 1. Subcellular Localization of Alcohol Dehydrogenase Activity in Pseudomonas putida S-5 Cells ", foundTables.get(0).getName());
        assertEquals("Table 2. Each of the two complete ORFS had a putative ", foundTables.get(1).getName());
        System.out.println("Names match.");
    }

    @Test
    public void findSpaceDistance(){
        double totalCharLength = 0.0;
        for(Element span : spans){
            String pos = span.attr("title");
            String[] positions = pos.split("\\s+");
            String word = span.text();
            int X1 = Integer.parseInt(positions[1]);
            int X2 = Integer.parseInt(positions[3]);
            int length = X2 -X1;
            if(word.length() > 0){
                double charLength = length/word.length();
                totalCharLength = totalCharLength + charLength;
            }
        }
        Double avgCharLength = totalCharLength/spans.size();
        assertEquals("35.874534161490686",avgCharLength.toString());
        System.out.println("AVG length of characters matches.");
    }


}

