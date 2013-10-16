package program2;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import program.HeaderMethods;
import program.Kinetics;
import program.Purification;
import program.RelativeActivity;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created for project: TableExtraction
 * In package: program2
 * Created with IntelliJ IDEA.
 * User: Sander van Boom
 * Date: 15-10-13
 * Time: 16:02
 */
/*
 * The Page class is simply the page as it is being read by the main class. As soon as it detects a table it
 * will create a Table class. It will parse the Table class it's own attributes to make sure the table can search in it's
 * Location etc.
 */
public class Page {
    private int LDDistance;
    private Elements spans;
    private String fileLocation;
    //TODO: create a method for the detection of whitespace, so more information can be extracted succesfully.

    public Page(String fileLocation, int LDDDistance) throws IOException {
        //~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-
        //Now we need to read the file:

        File input = new File(fileLocation);
        Document doc = Jsoup.parse(input, "UTF-8", "http://example.com/");

        org.jsoup.select.Elements read = doc.select("span.ocrx_word");
        this.spans = read;

        //~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-
        //Now we need to set the LDDDistance.
        this.LDDistance = LDDDistance;
        this.fileLocation = fileLocation;
    }

    //This method can be called to change the LDDistance.
    public void setLDDistance(int newLDDistance){
        this.LDDistance = newLDDistance;
    }

    //This method returns the current LDDistance.
    public int getLDDistance(){
        return LDDistance;
    }

    //This method can be called to change the fileLocation.
    public void setFileLocation(String newFileLocation){
        this.fileLocation = newFileLocation;
    }

    //This method returns the current fileLocation.
    public String getFileLocation(){
        return fileLocation;
    }

    //This method will find the tables in the content. It creates a table object for every table it finds.
    //The method returns null if it doesn't find a table.
    //TODO: support multiple tables in the same page.
    public Table createTables() throws IOException {
        System.out.println("Going to create tables now!");
        ArrayList<ArrayList<String>> foundTableLocations = new ArrayList<ArrayList<String>>();
        String word = "";
        boolean foundATable = false;
        Elements tableSpans = new Elements();
        Table foundTable = null;
        for(Element span : spans){
            word = span.text();
            try{
            if(word.substring(0, 5).equals("TABLE")||word.substring(0, 5).equals("table")||word.substring(0, 5).equals("Table")){
                foundATable = true;
                tableSpans.add(span);
            }
            }
            catch(StringIndexOutOfBoundsException e){

            }
            if (foundATable){
                tableSpans.add(span);
            }
        }
        if(foundATable){
            foundTable = new Table(LDDistance, tableSpans);
        }
        return foundTable;
    }
}
