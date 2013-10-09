package program;

/**
 * Created for project: TableExtraction
 * In package: program
 * Created with IntelliJ IDEA.
 * User: Sander van Boom
 * Date: 9-10-13
 * Time: 12:14
 */
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

//import opennlp.tools.formats.ad.ADSentenceStream.Sentence;
//import opennlp.tools.postag.POSModel;
//import opennlp.tools.postag.POSTaggerME;
//import opennlp.tools.sentdetect.SentenceDetectorME;
//import opennlp.tools.sentdetect.SentenceModel;
//import opennlp.uima.sentdetect.SentenceDetector;


public class RestructureUsingNLP {
    //private SentenceDetector openNLPSentenceDetector;
    public RestructureUsingNLP(){

    }

    public ArrayList evaluateSentence(String[] line){
        //System.out.println(line.length);
        ArrayList pattern = new ArrayList();
        MetaboliteDatabase MD = new MetaboliteDatabase();
        try{
            for(int x = 0;x<line.length;x++){
                //System.out.println(line[x]);

                //OPTIONAL: CHANGE , to . to make them DOUBLES
                if(line[x].contains(",")){
                    int index = 0;
                    index = line[x].indexOf(",");
                    //System.out.println(line[x] + " becomes: ");
                    line[x] = line[x].replace(",", ".");
                    //System.out.println(line[x]+".");
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
        //For testing
        //	for(int p =0;p<pattern.size();p++){
        //		System.out.print(pattern.get(p));
        //	}
        return pattern;


        //String[] tags = null;
        //tags = tagger(line);
        //return findNumericalData(tags);

    }
    /*
    public int sentenceDetector(String[] line) throws FileNotFoundException{
        //reading the model.
        InputStream modelIn = new FileInputStream("C:\\Users\\Sander van Boom\\Dropbox\\Tables and Figures\\Eclipse Workspace\\CSVRestructureAndSemantics\\src\\en-sent.bin");
       // SentenceModel model =null;
        try {
            //model = new SentenceModel(modelIn);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (modelIn != null) {
                try {
                    modelIn.close();
                }
                catch (IOException e) {
                }
            }
        }
        String text ="";
        int sentence =0;
        for(int x = 0;x <line.length;x++){
            text = text+line[x]+" ";
        }
        SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
        for(String sentenceString : sentenceDetector.sentDetect(text))
        {
            //A newline at the start of the sentence causes an error for some unknown reason
            if(sentenceString.charAt(0) == '\n')
            {
                sentenceString = sentenceString.substring(1);
            }
            sentence += 1;
            //System.out.println("NLP SENTENCE:"+sentenceString);

        }
        return sentence;
    }
    */

    /*
    private String[] tagger(String[] line){
        InputStream modelIn = null;
        POSModel model =null;
        try {
            modelIn = new FileInputStream("C:\\Users\\Sander van Boom\\Dropbox\\Tables and Figures\\Eclipse Workspace\\CSVRestructureAndSemantics\\src\\en-pos-maxent.bin");
            model = new POSModel(modelIn);
        }
        catch (IOException e) {
            // Model loading failed, handle the error
            e.printStackTrace();
        }
        finally {
            if (modelIn != null) {
                try {
                    modelIn.close();
                }
                catch (IOException e) {
                }
            }
        }
        POSTaggerME tagger = new POSTaggerME(model);
        String tags[] = tagger.tag(line);
        return tags;

    }
    */
    private boolean findNumericalData(String[] tags){
        int cders=0;
        for(int x = 0; x<tags.length;x++){
            //for testing:
            //System.out.print(line[x]+" : ");
            //System.out.println(tags[x]);
            if(tags[x].contains("CD")){
                cders++;
            }
            //System.out.println(tags[x]);
            if(cders >= 20){
                //For testing purpose:
                //System.out.println("NUMBERS IN THIS LINE: " + cders);
                //System.out.println("This must be the actual data!");
                return true;
            }
        }
        //System.out.println("Not enough numbers to be data");
        System.out.println(cders);
        return false;
    }
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
