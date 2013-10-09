package program;

/**
 * Created for project: TableExtraction
 * In package: program
 * Created with IntelliJ IDEA.
 * User: Sander van Boom
 * Date: 9-10-13
 * Time: 12:14
 */
import java.util.ArrayList;

public class RestructureUsingNLP {
    public RestructureUsingNLP(){

    }
    public ArrayList evaluateSentence(String[] line){
        ArrayList<String> pattern = new ArrayList();
        try{
            for(int x = 0;x<line.length;x++){
                if(line[x].contains(",")){
                    line[x] = line[x].replace(",", ".");
                }

                try{
                    Double.parseDouble(line[x]);
                    pattern.add("N");}
                catch(NumberFormatException nme)
                {

                    if(isNumber(line[x])){
                        pattern.add("N");
                    }
                    else{
                        //System.out.println(line[x]);
                        if(line[x].equals("ND")||line[x].equals("|")||line[x].contains(">")||line[x].contains("<")||line[x].contains("N/D")||line[x].contains(".")||(line[x].contains("±")&&isNumber(line[x+1]))||(line[x].equals("-") && isNumber(line[x]))||line[x]=="NA"||line[x]=="/"){
                            pattern.add("N");
                        }else{
                            pattern.add("S");
                        }
                    }
                }
            }
        }
        catch (NullPointerException e){

        }
        return pattern;

    }
    /*
     *The isNumber method returns true if the given String is actually a number.
     */

    //TODO: Create test cases for isNumber and twoSentencePattern

    public static boolean isNumber(String string) {
        return string.matches("^\\d+$");
    }
    public boolean twoSentencePattern(ArrayList sentencePattern1, ArrayList sentencePattern2){
        if(sentencePattern1.equals(sentencePattern2)){
            return true;
        }
        return false;
    }
    public void printPattern(ArrayList<ArrayList<String>> textPattern){
        for(int y = 0; y<textPattern.size();y++){
            for(int x=0;x<textPattern.get(y).size();x++){
                System.out.print(textPattern.get(y).get(x));
            }
            System.out.println();
        }
    }

    //NOTE: sentence 1 and sentence 2 must of the same length.
    //Otherwise it can't be a pattern!
    //The score pattern works on the assumption that tables contain a high ammount of numerical data and metabolites compared to
    //Ordinary sentences. Therefor we give points to each cell that has been marked up as a number or metabolite.
    public int scorePattern(ArrayList sentence1){
        int score = 0;
        for(int x =0; x<sentence1.size(); x++){
            if(sentence1.contains("M")){
                score = score +4;
            }
            if(sentence1.contains("N")){
                score = score + 2;
            }

        }
        return score;
    }
}
