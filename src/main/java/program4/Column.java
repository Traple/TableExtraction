package program4;

import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.logging.Logger;

public class Column {
    public static Logger LOGGER = Logger.getLogger(Column.class.getName());
    private ArrayList<Element> column;
    private ArrayList<Element> firstElementInEachRow;
    private double averageHeightOfWords;
    private double heightThreshold;
    private ArrayList<Integer> possibleHeaderSeparationLines;
    private String IDOfFirstCellOfDataInColumn;
    private ArrayList<Element> headerCells;
    private ArrayList<Element> data;
    private double thresholdConstant;

    public Column(ArrayList<Element> column){
        System.out.println("-------------------------------------------------New Column!-----------------------------------------------------");

        this.column = column;
        this.thresholdConstant = 0.8;

        this.averageHeightOfWords = calculateAverageHeightOfWords();
        setAVGHeightThreshold();

        LOGGER.info("The average Height of words is: " + averageHeightOfWords);
        System.out.println("The average Height of words is: " + averageHeightOfWords);

        setFirstElementInEachRow();
        //System.out.println(firstElementInEachRow);
        this.possibleHeaderSeparationLines = calculatePossibleHeaderSeperationLines();
        setHeaderBreakingPoint();

        //System.out.println("The first cell in the data for this column is: " + IDOfFirstCellOfDataInColumn);
        this.headerCells = new ArrayList<Element>();
        this.data = new ArrayList<Element>();
        separateHeadersFromData();

        printColumn();
    }

    private double calculateAverageHeightOfWords(){
        int Y1;
        int Y2;
        int totalHeight = 0;
        for(Element cell : column){
            String pos = cell.attr("title");
            String[] positions = pos.split("\\s+");
            Y1 = Integer.parseInt(positions[2]);
            Y2 = Integer.parseInt(positions[4]);

            totalHeight = totalHeight + (Y2 - Y1);
        }
        return totalHeight/column.size();
    }

    public void setAVGHeightThreshold(){
        this.heightThreshold = averageHeightOfWords*thresholdConstant;
    }

    private ArrayList<Integer> calculatePossibleHeaderSeperationLines(){
        ArrayList<Integer> possibleHeaderSeperationLine = new ArrayList<Integer>();
        int lastY2 =0;
        for(Element cell : firstElementInEachRow){
            String pos = cell.attr("title");
            String[] positions = pos.split("\\s+");
            int Y1 = Integer.parseInt(positions[2]);
            int Y2 = Integer.parseInt(positions[4]);

            if(lastY2 != 0){
                int distance = Y1 -  lastY2;
                possibleHeaderSeperationLine.add(distance);
            }
            lastY2 = Y2;
        }
        return possibleHeaderSeperationLine;
    }

    private void setFirstElementInEachRow(){
        ArrayList<Element> firstInRows = new ArrayList<Element>();
        int lastY1 =0;
        int lastY2 =0;
        for(int x =0; x<column.size();x++){
            String pos = column.get(x).attr("title");
            String[] positions = pos.split("\\s+");
            int Y1 = Integer.parseInt(positions[2]);
            int Y2 = Integer.parseInt(positions[4]);
            if((Y1 >= lastY1-heightThreshold && Y1<=lastY1+heightThreshold) || (Y2 >= lastY2-heightThreshold && Y2<=lastY2+heightThreshold)){
                //What's this? You're not on the list! - General Talius
            }
            else{
                firstInRows.add(column.get(x));
            }
            lastY1 = Y1;
            lastY2 = Y2;
        }
       this.firstElementInEachRow = firstInRows;
    }

    private void setHeaderBreakingPoint(){
        int counter = 0;
        String firstCellInData = "";
        for(int distanceBetweenLines : possibleHeaderSeparationLines){
            if(distanceBetweenLines > heightThreshold){
                firstCellInData = firstElementInEachRow.get(counter+1).attr("id");
                //firstCellInData = firstElementInEachRow.get(counter+1).text();
                break;
            }
            counter++;
        }
        IDOfFirstCellOfDataInColumn = firstCellInData;
    }

    public ArrayList<Element> returnHeaderCells(){
        ArrayList<Element> HeaderCells = new ArrayList<Element>();
        for(Element cell : column){
            if(cell.attr("id")==IDOfFirstCellOfDataInColumn){
                break;
            }
            HeaderCells.add(cell);
        }
        return HeaderCells;
    }

    public void separateHeadersFromData(){
        boolean thisIsData = false;
        for(Element cell : column){
            if(cell.attr("id")==IDOfFirstCellOfDataInColumn&&!thisIsData){
                thisIsData = true;
                data.add(cell);
            }
            else if(thisIsData){
                data.add(cell);
            }
            else if(!thisIsData){
                headerCells.add(cell);
            }
        }
    }
    public void printColumn(){
        System.out.print("My header is: " );
        for(Element cell: headerCells){
            LOGGER.info("This column has header: " + cell.text());
            System.out.print(cell.text());
        }
        System.out.println();
        System.out.println("Which has the following values: ");
        for(Element cell: data){
            LOGGER.info("Data: " + cell.text());
            System.out.print(cell.text() + ", ");
        }
        System.out.println();
    }
}
