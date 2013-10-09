package loft;

/**
 * Created for project: TableExtraction
 * In package: loft
 * Created with IntelliJ IDEA.
 * User: Sander van Boom
 * Date: 9-10-13
 * Time: 17:06
 */
/*

//import opennlp.tools.formats.ad.ADSentenceStream.Sentence;
//import opennlp.tools.postag.POSModel;
//import opennlp.tools.postag.POSTaggerME;
//import opennlp.tools.sentdetect.SentenceDetectorME;
//import opennlp.tools.sentdetect.SentenceModel;
//import opennlp.uima.sentdetect.SentenceDetector;
 */
public class NLPMethods {
           /*//private SentenceDetector openNLPSentenceDetector;
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
      */
}
