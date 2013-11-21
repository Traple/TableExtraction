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
    private int Y1OfLastWord;
    private ArrayList<Integer> distances;

    public Line(Elements words, double lineThreshold){
        this.words = words;
        this.lineThreshold = lineThreshold;
        this.wordTypes = new ArrayList<ArrayList<String>>();
        this.thresholdModifier = 2;
        ClusterColumns();

        String[] positions;
        String pos = words.get(0).attr("title");
        positions = pos.split("\\s+");
        this.Y1OfFirstWord = Integer.parseInt(positions[2]);

        pos = words.get(words.size()-1).attr("title");
        positions = pos.split("\\s+");
        this.Y1OfLastWord = Integer.parseInt(positions[2]);

        //setLineTypes();

    }

    private void ClusterColumns(){
        String pos = words.get(0).attr("title");
        String[] positions;
        positions = pos.split("\\s+");
        int lastX2 = Integer.parseInt(positions[3]);
        ArrayList<Element> cluster = new ArrayList<Element>();
        ArrayList<Integer> distances = new ArrayList<Integer>();
        clusters = new ArrayList<ArrayList<Element>>();

        for(Element word : words){
            pos = word.attr("title");
            positions = pos.split("\\s+");
            int x1 = Integer.parseInt(positions[1]);
            int x2 = Integer.parseInt(positions[3]);
            int distance = -(lastX2 - x1);

            if((distance) >lineThreshold*thresholdModifier){
                distances.add(distance);
                clusters.add(cluster);
                cluster = new ArrayList<Element>();
                cluster.add(word);
            }
            else{
                cluster.add(word);
            }
            lastX2 = x2;
        }
        if(clusters.size()>=1&&!clusters.get(0).equals(cluster)){
            clusters.add(cluster);                                   //TODO: Test this rule a bit more.
        }
        this.distances = distances;
    }


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

    public static int getClusterX1(ArrayList<Element> cluster){
            int clusterBoundaryX1 = Integer.MAX_VALUE;
            String pos;
            String[] positions;

            Element firstWordInCell = cluster.get(0);

            pos = firstWordInCell.attr("title");
            positions = pos.split("\\s+");
            int x1 = Integer.parseInt(positions[1]);
            if(x1 < clusterBoundaryX1){
                clusterBoundaryX1 = x1;
            }
        return clusterBoundaryX1;
        }

    public static int getClusterX2(ArrayList<Element> cluster){
        int clusterBoundaryX2 = Integer.MIN_VALUE;
        String pos;
        String[] positions;

        Element lastWordInCell = cluster.get(cluster.size()-1);

        pos = lastWordInCell.attr("title");
        positions = pos.split("\\s+");
        int x2 = Integer.parseInt(positions[3]);
        if(x2 > clusterBoundaryX2){
            clusterBoundaryX2 = x2;
        }
        return clusterBoundaryX2;
    }

    public int getClusterSize(){
        return clusters.size();
    }

    public ArrayList<String> getClusterTypes(){
        return clusterTypes;
    }

    //TODO: The toString method requires very good documentation as described in Effective Java P75.
    //Also create more getters that relate to toString as described on the next page.
    public String toString(){
        String line = "";
        for(Element word : words){
            line = line + word.text() + " ";
        }
        return line;
    }
    public int getY1OfFirstWord(){
        return Y1OfFirstWord;
    }
    public int getY1OfLastWord(){
        return Y1OfLastWord;
    }

    //mainly used for the Validation object that has been made in the Table class.
    public ArrayList<Integer> getDistances(){
        return distances;
    }
    public double getDistanceThreshold(){
        return thresholdModifier * lineThreshold;
    }
}
