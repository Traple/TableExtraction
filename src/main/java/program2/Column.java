package program2;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import program.Purification;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created for project: TableExtraction
 * In package: program2
 * Created with IntelliJ IDEA.
 * User: Sander van Boom
 * Date: 15-10-13
 * Time: 16:05
 */
/*
 * The Column class contains method and attributes that have to do with various columns inside a table.
 * Every Column contains:
 * An X1 location (right border of the column)
 * An X2 location (left border of the column)
 * An Y1 location (the top of the column)
 * An Y2 location (the bottom of the column)
 *
 * Every column can:
 * Rearrange it's boundaries (by checking intersecting words)
 * In order to do so it needs:
 *
 */
public class Column {
    private double X1;                                     //The locations of the start of the header.
    private double X2;
    private double Y1;
    private double Y2;
    private String header;
    private CommonMethods CM;
    private Elements spans;
    private Collection<Purification> Pheaders;
    private String headerType;
    private ArrayList<Cell> cells;

    public Column(String header, double X1, double X2, double Y1, double Y2, Elements spans, Collection<Purification> Pheaders){
        this.X1 = X1;
        this.X2 = X2;
        this.Y1 = Y1;
        this.Y2 = Y2;
        this.header = header;
        this.CM = new CommonMethods();
        this.spans = spans;
        this.Pheaders = Pheaders;
        System.out.println("Column " + header + " created.");
        System.out.println("I start at: " + X1);

    }

    /*
     * The columnChecker extracts the content directly below the column.
     */
    public ArrayList<ArrayList<String>> columnChecker(){
        ArrayList<ArrayList<String>> columnContent = new ArrayList<ArrayList<String>>();

        String[] positionsS = null;
        for(Element span : spans){
            String pos = span.attr("title");
            positionsS = pos.split("\\s+");
            double doupje =0.0 ;
            double doupje2 =0.0 ;
            String positionsOfContentLine = "";
            for(int i = 0; i<positionsS.length;i++){
                if(positionsS[i].equals("bbox")){
                    //do nothing
                }
                else{
                    Integer I = Integer.parseInt(positionsS[1]);
                    Integer I2 = Integer.parseInt(positionsS[2]);
                    doupje = (I.doubleValue());
                    doupje2 = (I2.doubleValue());
                }
            }
            if(doupje>= X1 && doupje <=(X1+CM.calcDistance(X1, X2))&&doupje2 >= Y2){
                ArrayList<String> columnContentPositions = new ArrayList<String>();
                ArrayList<String> columnContentString = new ArrayList<String>();
                columnContentString.add(span.text());
                positionsOfContentLine = span.attr("title");
                columnContentPositions.add(positionsOfContentLine);
                columnContent.add(columnContentString);
                columnContent.add(columnContentPositions);
            }
        }

        //Before we return the columnContent we want to refine it. Some cells may be split up. We want every word that is
        //in the same row to be in the same spot in the array.
        columnContent = mergeWordsOnSameLine(columnContent);

        return columnContent;
    }

    /*
     * mergeWordsOnSameLine merges words in the columnContent that are on the same line.
     * NOTE: This does NOT change anything in the SPANS. ONLY in the ArrayList!!!
     * AFTER this method is being run, the Array and the OCR-HTML do no longer match!!!!
     */

    public ArrayList<ArrayList<String>> mergeWordsOnSameLine(ArrayList<ArrayList<String>> columnContent){
        int lastY1 = 0;
        int loopLength = columnContent.size();
        for(int x = 0; x< loopLength; x++){
            String pos = "";
            String[] positions;
            String newWord = "";
            if(x%2 == 1){
                pos = columnContent.get(x).get(0);
                positions = pos.split("\\s+");
                if(5 > Integer.parseInt(positions[2]) - lastY1){
                    columnContent.get(x-3).add(0, columnContent.get(x - 3).get(0) + columnContent.get(x - 1).get(0));
                    columnContent.get(x-3).remove(1);
                    columnContent.remove(x - 1);
                    columnContent.remove(x - 1);
                    loopLength = loopLength -2;
                    x = x -1;
                }
                else{
                    lastY1 = Integer.parseInt(positions[2]);
                }
            }

        }

        return columnContent;
    }
    /*
    * This method returns the type correlating with the header.
    */
    public String findHeaderTypes(){
        String headerType = "";
            for(Purification p : Pheaders){
                ArrayList<String> syns = new ArrayList<String>();
                String synType = "";
                for(int i = 0;i<p.getSynonyms().length;i++){
                    syns.add(p.getSynonyms()[i]);
                    synType = p.getTypes()[0];
                }
                if(syns.contains(header)){
                    headerType= synType;
                }
            }

        return headerType;
    }
    /*
     * This method creates new cells. Because every cell has a position and content this has to be extracted from the column collection.
     */
    public ArrayList<Cell> fillCells(ArrayList<ArrayList<String>> column){
        ArrayList<Cell> cellList = new ArrayList<Cell>();
        String content = "";
        String position = "";
        Cell newCell;
        for(int x = 0; x<column.size();x++){
            if(x%2 == 0){
                content = column.get(x).get(0);
            }
            else if(x%2 == 1){
                position = column.get(x).get(0);
                newCell = new Cell(content, position);
                cellList.add(newCell);
            }
        }
        this.cells = cellList;
        return cells;
    }

}
