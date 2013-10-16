package program2;

import program.LevenshteinDistance;

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
public class Main {
    public static void main(String[] args) throws IOException {

        //The following variables should be changed to commandline parameters in the future:
        String fileLocation = "C:\\Users\\Sander van Boom\\Documents\\School\\tables\\OCR\\OCR\\33-3.html";
        int MaxLevenshteinDistance = 0;
        Page page = new Page(fileLocation, MaxLevenshteinDistance);
        page.createTables();
    }
}
