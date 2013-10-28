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


    //Welcome to T.E.A. 0.3
public class Main {
    public static void main(String[] args) throws IOException {
        //The following variables should be changed to commandline parameters in the future:
        String fileLocation =  "C:\\Users\\Sander van Boom\\Documents\\School\\tables\\T.E.A. 0.4 test\\24089145-3.html";

        Page page = new Page(fileLocation);
        page.createTables();

    }
}
