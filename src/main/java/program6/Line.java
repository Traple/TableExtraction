package program6;


import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class Line {
    private Elements words;
    private double lineThreshold;
    private ArrayList<ArrayList<Element>> clusters;
    private ArrayList<ArrayList<String>> wordTypes;
    private double thresholdModifier;
    private ArrayList<String> clusterTypes;
    private int Y1OfFirstWord;

    public Line(Elements words, double lineThreshold){
        this.words = words;
        this.lineThreshold = lineThreshold;
        this.wordTypes = new ArrayList<ArrayList<String>>();
        this.thresholdModifier = 4;
        ClusterColumns();

        String[] positions;
        String pos = words.get(0).attr("title");
        positions = pos.split("\\s+");
        this.Y1OfFirstWord = Integer.parseInt(positions[2]);

        setLineTypes();

        /**
        System.out.println("I found " + (clusters.size()+1)+ " clusters.");

        for(String word : wordTypes){
            System.out.print(word + " ");
        }
        System.out.println();
         */
    }

    private void ClusterColumns(){
        String pos = words.get(0).attr("title");
        String[] positions;
        positions = pos.split("\\s+");
        int lastX2 = Integer.parseInt(positions[3]);
        ArrayList<Element> cluster = new ArrayList<Element>();
        clusters = new ArrayList<ArrayList<Element>>();

        for(Element word : words){
            pos = word.attr("title");
            positions = pos.split("\\s+");
            int x1 = Integer.parseInt(positions[1]);
            int x2 = Integer.parseInt(positions[3]);
            int distance = lastX2 - x1;

            if(-(distance) >lineThreshold*thresholdModifier){
                clusters.add(cluster);
                cluster = new ArrayList<Element>();
                cluster.add(word);
            }
            else{
                cluster.add(word);
            }
            lastX2 = x2;
        }
        if(clusters.size()>3){
            clusters.add(cluster);                                   //TODO: Test this rule a bit more.
        }
    }


    //TODO: Finish the rest of the test.
    private void setLineTypes(){
        ArrayList<String> clusterTypes = new ArrayList<String>();
        int strings = 0;
        int ints = 0;

        for(ArrayList<Element> cluster : clusters){
            for(Element word : cluster){
                String textOfWord = word.text();
                String typeOfWord = getTypeOfWord(textOfWord);
                if(typeOfWord.equals("N")){
                    ints+=1;
                }
                else{
                    strings+=1;
                }
            }
        if(ints>strings){
            clusterTypes.add("N");
        }
        else{
            clusterTypes.add("S");
        }
        }
        this.clusterTypes = clusterTypes;
    }

    private String getTypeOfWord(String word){
        String type;
        if(CommonMethods.containsNumber(word)){
            type = "N";
        }
        else{
            type = "S";
        }
        return type;
    }

    public ArrayList<ArrayList<Element>> getClusters(){
        return clusters;
    }

    public ArrayList<String> getClusterTypes(){
        return clusterTypes;
    }
    public String getLine(){
        String line = "";
        for(Element word : words){
            line = line + word.text() + " ";
        }
        return line;
    }
    public int getY1OfFirstWord(){
        return Y1OfFirstWord;
    }
}
