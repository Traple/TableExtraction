package program3;

import java.io.IOException;

/**
 * Created for project: TableExtraction
 * In package: program2
 * Created with IntelliJ IDEA.
 * User: Sander van Boom
 * Date: 15-10-13
 * Time: 16:30
 */
/*
 * The main class parses the pages so the tables can be extracted.
 */

//TODO: Find a name for this software!

public class Main {
    public static void main(String[] args) throws IOException {
        //The following variables should be changed to commandline parameters in the future:
        String fileLocation = "C:\\Users\\Sander van Boom\\Documents\\School\\tables\\OCR\\OCR\\36-3.html";
        Page page = new Page(fileLocation);
        page.createTables();

    }
}
