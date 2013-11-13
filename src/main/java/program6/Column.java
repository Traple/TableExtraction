package program6;

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

    /**
     * This constructor takes the spans that are within the boundaries (X1 and X2) and calls its methods to create the column.
     * @param column a list of elements (words) that are within the X1 and X2 boundaries
     */
    public Column(ArrayList<Element> column){
        System.out.println("-------------------------------------------------New Column!-----------------------------------------------------");

        this.column = column;
        this.thresholdConstant = 1.0;

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

    /**
     * This method calculates the avarage height of words. It takes the private elements that have been assigned in the constructor
     * and checks the avarage height of each word. This can later be used to check where the header of the column is.
     * @return the avarage height of the words inside the column.
     */
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

    /**
     * This method creates a list with the locations where the headers might be separated from the data.
     * Other methods will have to check the actual breaking point.
     * @return A list with the locations where the headers might be separated from the data.
     */
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

    /**
     * This method find the first word in each row. This is decisive for separating the header from the data.
     */
    private void setFirstElementInEachRow(){
        ArrayList<Element> firstInRows = new ArrayList<Element>();
        int lastY1 =0;
        int lastY2 =0;
        for (Element aColumn : column) {
            String pos = aColumn.attr("title");
            String[] positions = pos.split("\\s+");
            int Y1 = Integer.parseInt(positions[2]);
            int Y2 = Integer.parseInt(positions[4]);
            if ((Y1 >= lastY1 - heightThreshold && Y1 <= lastY1 + heightThreshold) || (Y2 >= lastY2 - heightThreshold && Y2 <= lastY2 + heightThreshold)) {
                //What's this? You're not on the list! - General Talius
            } else {
                firstInRows.add(aColumn);
            }
            lastY1 = Y1;
            lastY2 = Y2;
        }
       this.firstElementInEachRow = firstInRows;
    }

    /**
     * This method decides the breaking point, the breaking point is when the distance between words is great enough to
     * assume that it is the place where headers are separated from the data.
     */
    private void setHeaderBreakingPoint(){
        int counter = 0;
        String firstCellInData = "";
        for(int distanceBetweenLines : possibleHeaderSeparationLines){
            if(distanceBetweenLines > heightThreshold){
                firstCellInData = firstElementInEachRow.get(counter+1).attr("id");
                break;
            }
            counter++;
        }
        IDOfFirstCellOfDataInColumn = firstCellInData;
    }

    /**
     * This method supports the separation of the header from the actual data by changing private variables
     * It does this by checking if the data is passing the height threshold.
     */
    private void separateHeadersFromData(){
        boolean thisIsData = false;
        for(Element cell : column){
            if(cell.attr("id").equals(IDOfFirstCellOfDataInColumn) &&!thisIsData){
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

    /**
     * This void method prints the found column. Might be removed in later versions of TEA.
     */
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

    /**
     * This method returns the content of the column (in XML) so it can be written to a output file.
     * @return This method returns the content of the column as a String. This can be used for the XML output file.
     */
    public String getColumnContentInXML(){
        String header = "";
        boolean noData = true;
        for(Element cell: headerCells){
            header = header + cell.text();
        }
        String content = "<header>"+header+"</header>\n";
        for(Element cell: data){
            noData = false;
            content = content + "<cell>"+cell.text() + "</cell>\n";
        }
        if(noData){
            return null;
        }

        return content;
    }
    public double getHeightThreshold(){
        return heightThreshold;
    }
    //TODO: Add a non semenatic content of table validation.
}
