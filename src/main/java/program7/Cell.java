package program7;

import org.jsoup.nodes.Element;

import java.util.ArrayList;

//This class is mainly used for storing the information about the cells. This makes it easier to represent cells directly form a list
//because the toString method for each cell will be called as well.
public class Cell {

    private ArrayList<String> content;
    private int additionScore;
    private ArrayList<Element> spans;
    private int y1;
    private int y2;
    private int x1;
    private int x2;

    /**
     * This constructor stores the content as well as the addition score of the given cell.
     * Because not every cell has a score like this, the class also contains a second constructor.
     * @param content A list containing the HTML words of the cell.
     * @param additionScore The score that was used to add this cell to the table.
     */
    public Cell(ArrayList<Element> content, int additionScore){
        this.additionScore = additionScore;
        ArrayList<String> newContent = new ArrayList<String>();
        for(Element word : content){
            newContent.add(word.text());
        }
        this.content = newContent;
        this.spans = content;
        calculatePositions();
    }

    /**
     * This constructor only stores the content of a cell. Some cells might not have a addition score (because they were
     * already in the first iteration).
     * @param content A list of HTML words that forms the content of this cell.
     */
    public Cell(ArrayList<Element> content){
        this.additionScore = 0;
        ArrayList<String> newContent = new ArrayList<String>();
        for(Element word : content){
            newContent.add(word.text());
        }
        this.content = newContent;
        this.spans = content;
        calculatePositions();
    }

    /**
     * This method calculates the boundaries of the cell by looking at the X and Y coordinates of the words in the cell.
     */
    private void calculatePositions(){
        String pos;
        String[] positions;
        int highestX2 = Integer.MIN_VALUE;
        int lowestX1 = Integer.MAX_VALUE;
        int highestY2 = Integer.MIN_VALUE;
        int lowestY1 = Integer.MAX_VALUE;
        int x1;
        int x2;
        int y1;
        int y2;

        for(Element span : spans){
            pos = span.attr("title");
            positions = pos.split("\\s+");
            x1 = Integer.parseInt(positions[1]);
            x2 = Integer.parseInt(positions[3]);
            y1 = Integer.parseInt(positions[2]);
            y2 = Integer.parseInt(positions[4]);

            if(x1 < lowestX1){
                lowestX1 = x1;
            }
            if(x2 > highestX2){
                highestX2 = x2;
            }
            if(y1 < lowestY1){
                lowestY1 = y1;
            }
            if(y2 > highestY2){
                highestY2 = y2;
            }
        }
        this.x1 = lowestX1;
        this.x2 = highestX2;
        this.y1 = lowestY1;
        this.y2 = highestY2;
    }
    public int getY1(){
    return y1;
    }
    public int getY2(){
        return y2;
    }
    public int getX1(){
        return x1;
    }
    public int getX2(){
        return x2;
    }

    /**
     * This is the toString method of the cell class. It shows the content of each cell as well as it's addition score.
     * @return A string representing the content and the addition score.
     */
    public String toString(){
        String cell = "";
        for(String word : content){
            cell = cell + word + ": " + additionScore + "||";
        }

        return cell.substring(0,cell.length()-1);
    }
}
