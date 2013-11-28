package loft;

import org.jsoup.nodes.Element;
import program7.CommonMethods;

import java.util.ArrayList;

/**
 * Created for project: TableExtraction
 * In package: loft
 * Created with IntelliJ IDEA.
 * User: Sander van Boom
 * Date: 28-11-13
 * Time: 12:45
 */
public class Line {

  /*
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
      public ArrayList<String> getClusterTypes(){
        return clusterTypes;
    }
*/
}
