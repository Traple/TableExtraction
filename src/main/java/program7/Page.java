package program7;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

/*
 * The Page class is simply the page as it is being read by the main class. As soon as it detects a table it
 * will create a Table2 class. It will parse the Table2 class it's own attributes to make sure the table can search in it's
 * Location etc.
 */
public class Page {

    private Elements spans;
    private String workLocation;
    private File file;
    private double spaceDistance;
    public static Logger LOGGER = Logger.getLogger(Page.class.getName());

    /**
     * The constructor of this class sets the local variables for this class and creates a Jsoup document.
     * @param file The HTML file created by the OCR.
     * @param workLocation The work location as specified by the user.
     * @throws java.io.IOException
     */
    public Page(File file, String workLocation) throws IOException {
        Document doc = Jsoup.parse(file, "UTF-8", "http://example.com/");

        this.spans = doc.select("span.ocrx_word");
        this.workLocation = workLocation;
        this.file = file;
        this.spaceDistance = findSpaceDistance();
        LOGGER.info("The found average length of a character is: " + getSpaceDistance());
    }

    /**
     * This method will find the tables in the content. It creates a table object for every table it finds.
     * Do note that it creates a substring of the span(word) it finds. So if it finds <span>(table</span> it will not be detected.
     * This has been done to reduce the amount of false positives (for referring to a table in the text).
     * @return The Table2 object. This method returns an empty list if no table was found.
     * @throws java.io.IOException
     */
    public ArrayList<Table2> createTables() throws IOException {
        String word;
        boolean foundATable = false;
        Elements tableSpans = new Elements();
        ArrayList<Table2> foundTables = new ArrayList<Table2>();
        int tableID = 0;
        for (Element span : spans) {
            word = span.text();
            try {
                if(word.substring(0, 5).equals("TABLE") || word.substring(0, 5).equals("table") || word.substring(0, 5).equals("Table2")&&foundATable){
                    foundTables.add(new Table2(tableSpans, spaceDistance, file, workLocation, tableID)); //make a new table from the collected spans
                    tableSpans = new Elements();                                                    //reset the spans for the new Table2
                    tableSpans.add(span);
                }
                else if (foundATable) {
                    tableSpans.add(span);
                }
                if (word.substring(0, 5).equals("TABLE") || word.substring(0, 5).equals("table") || word.substring(0, 5).equals("Table2")&&!foundATable) {
                    foundATable = true;
                    tableSpans.add(span);
                }
            } catch (StringIndexOutOfBoundsException e) {
                if (foundATable) {
                    tableSpans.add(span);
                }
            }
            tableID++;
        }
        if (foundATable) {
            foundTables.add(new Table2(tableSpans, spaceDistance, file, workLocation, tableID));
        }
        if(!foundATable){
            LOGGER.info("There was no table found. ");
        }
        return foundTables;
    }

    /**
     * This method tries to find the average space/distance of a character in a table. This can be used to determine
     * the columns in a table.
     * @return the average distance/space of a character.
     */
    private double findSpaceDistance(){
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
        return totalCharLength/spans.size();
    }

    public double getSpaceDistance(){
        return spaceDistance;
    }
}
