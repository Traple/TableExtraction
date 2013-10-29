package program4;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/*
 * The Page class is simply the page as it is being read by the main class. As soon as it detects a table it
 * will create a Table class. It will parse the Table class it's own attributes to make sure the table can search in it's
 * Location etc.
 */
public class Page {
    private Elements spans;
    private String workLocation;
    public static Logger LOGGER = Logger.getLogger(Page.class.getName());
    //TODO: create a method for the detection of whitespace, so more information can be extracted succesfully.

    public Page(File file, String workLocation) throws IOException {
        //~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-
        //Now we need to read the file:

        Document doc = Jsoup.parse(file, "UTF-8", "http://example.com/");

        this.spans = doc.select("span.ocrx_word");
        this.workLocation = workLocation;
    }

    //This method will find the tables in the content. It creates a table object for every table it finds.
    //The method returns null if it doesn't find a table.
    //TODO: support multiple tables in the same page.
    public Table createTables() throws IOException {
        String word;
        boolean foundATable = false;
        Elements tableSpans = new Elements();
        Table foundTable = null;
        for (Element span : spans) {
            word = span.text();
            try {
                if (word.substring(0, 5).equals("TABLE") || word.substring(0, 5).equals("table") || word.substring(0, 5).equals("Table")) {
                    foundATable = true;
                    tableSpans.add(span);
                }
            } catch (StringIndexOutOfBoundsException e) {
                //do nothing
            }
            if (foundATable) {
                tableSpans.add(span);
            }
        }
        if (foundATable) {
            foundTable = new Table(tableSpans, workLocation);
        }
        if(!foundATable){
            LOGGER.info("There was no table found. ");
            System.out.println("No table found =(");
        }
        return foundTable;
    }
}
