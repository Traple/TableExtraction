package program7;

import org.jsoup.nodes.Element;

import java.util.ArrayList;

public class Cell {

    private ArrayList<String> content;
    private int additionScore;

    public Cell(ArrayList<Element> content, int additionScore){
        this.additionScore = additionScore;
        ArrayList<String> newContent = new ArrayList<String>();
        for(Element word : content){
            newContent.add(word.text());
        }
        this.content = newContent;
    }
    public int getAdditionScore(){
        return additionScore;
    }
    public String toString(){
        String cell = "";
        for(String word : content){
            cell = cell + word + ": " + additionScore + "||";
        }

        return cell.substring(0,cell.length()-1);
    }
}
