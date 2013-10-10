package program;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.geom.Point2D;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created for project: TableExtraction
 * In package: program
 * Created with IntelliJ IDEA.
 * User: Sander van Boom
 * Date: 10-10-13
 * Time: 11:05
 */
public class RestructureUsingOCR {
    private Set<String> purificationHeaders = new HashSet<String>();
    private Set<String> kineticHeaders = new HashSet<String>();
    private Set<String> activityHeaders = new HashSet<String>();
    private Collection<Purification> Pheaders;
    private Collection<Kinetics> Kheaders;
    private Collection<RelativeActivity> Aheaders;

    public RestructureUsingOCR() throws IOException {

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Purification>>(){}.getType();
        Type collectionType2 = new TypeToken<Collection<Kinetics>>(){}.getType();
        Type collectionType3 = new TypeToken<Collection<RelativeActivity>>(){}.getType();

        try{
            Reader reader;
            reader = new InputStreamReader(HeaderMethods.class.getResourceAsStream("/program/purification.json"), "UTF-8");
            this.Pheaders = gson.fromJson(reader, collectionType);
            for(Purification p: Pheaders){
                for(int u =0; u<p.getSynonyms().length;u++){
                    this.purificationHeaders.add(p.getSynonyms()[u]);
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void getInfo() throws IOException {
        File input = new File("C:\\Users\\Sander van Boom\\Dropbox\\Tables and Figures\\OCR\\31-2.html");
        Document doc = Jsoup.parse(input, "UTF-8", "http://example.com/");

        Element link = doc.select("span.ocr_line").first();
        Elements spans = doc.select("span.ocrx_word");

        String text = doc.body().text(); // "An example link"
        String linkHref = link.attr("title"); // "http://example.com/"
        String[] positions = null;

        for(Element span : spans){
            if(span.text().contains("TABLE")){
                String pos = span.attr("title");
                positions = pos.split("\\s+");
                System.out.println("Height of the beginning of Table: " + positions[2]);
                 }
            String word = span.text();
            for(String header:purificationHeaders){
                    if(word.contains(header)){
                        System.out.println("HEADER: " +header);
                        String pos = span.attr("title");
                        positions = pos.split("\\s+");
                        System.out.println(span.outerHtml());
                        System.out.println("Y header : " + positions[2]);
                        System.out.println("X header : " + positions[1]);
                        System.out.println("length: " + (Double.parseDouble(positions[3]) - Double.parseDouble(positions[1])));
                         }
                       }                            int count =0;
                        for(Purification p : Pheaders){
                            if(p.getUnits().length == 0){

                            }
                            else if(word.equals(p.getUnits()[count])){
                                System.out.println("Unit: " +p.getUnits()[count]);
                                String pos = span.attr("title");
                                positions = pos.split("\\s+");
                                System.out.println(span.outerHtml());
                                System.out.println("Y unit : " + positions[2]);
                                System.out.println("X unit : " + positions[1]);
                                System.out.println("length: " + (Double.parseDouble(positions[3]) - Double.parseDouble(positions[1])));
                                count++;
                            }
            }

        }
        }
        public ArrayList lineChecker(double begin, double length, String header) throws IOException {
            ArrayList columnLine = new ArrayList();
            File input = new File("C:\\Users\\Sander van Boom\\Dropbox\\Tables and Figures\\OCR\\31-2.html");
            Document doc = Jsoup.parse(input, "UTF-8", "http://example.com/");

            Elements spans = doc.select("span.ocrx_word");
            String[] positionsS = null;
            int[] positionsT = null;
            ArrayList positionsA = new ArrayList();
            int count = 0;
            for(Element span : spans){
                String pos = span.attr("title");
                positionsS = pos.split("\\s+");
                double doupje =0.0 ;
                for(int i = 0; i<positionsS.length;i++){
                    //System.out.println(positionsS[i]);
                    if(positionsS[i].equals("bbox")){

                    }
                    else{
                        Integer I = Integer.parseInt(positionsS[1]);
                        doupje = (I.doubleValue());

                    }
                }
                if(doupje>= begin && doupje <=(begin+length)){
                    //System.out.println("Doupje is bigger then: "+begin+" but smaller then: "+(begin+length)+" doupje is: "+doupje);
                    //System.out.println(span.text());
                    if(isNumber(span.text())){
                        System.out.println("Required information found!");
                        System.out.println(header + " = "+span.text());
                        columnLine.add(span.text());
                        System.out.println("found at: " + span.attr("title"));
                    }
                }
                count++;

            }
                return columnLine;
        }

    /*
     * This method is similar to the RestructureUsingNLP method, evalueSentence.
     * However in OCR we read in columns (derived from the headers) and now in lines.
     *
     */
    public ArrayList evalueteColumn(ArrayList<String> column){
        ArrayList<String> pattern = new ArrayList();
            try{
                for(int x = 0;x<column.size();x++){

                    try{
                        Double.parseDouble(column.get(x));
                        pattern.add("N");}
                    catch(NumberFormatException nme)
                    {

                        if(isNumber(column.get(x))){
                            pattern.add("N");
                        }
                        else{
                            //System.out.println(line[x]);
                            if(column.get(x).equals("ND")||column.get(x).equals("|")||column.get(x).contains(">")||column.get(x).contains("<")||column.get(x).contains("N/D")||column.get(x).contains(".")||(column.get(x).contains("±")&&isNumber(column.get(x+1)))||(column.get(x).equals("-") && isNumber(column.get(x)))||column.get(x)=="NA"||column.get(x)=="/"){
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

        public double calcDistance(double x1, double x2){
            double distance = 0.0;
            if(x1 > x2){
                distance = x1 - x2;
            }
            else if(x2 > x1){
                distance = x2 - x1;
            }
            return distance;
        }

    public static boolean isNumber(String string) {
        //Pattern pattern = Pattern.compile("^(\\d+.*|-\\d+.*)");
        Pattern pattern = Pattern.compile("^(\\d+.*|-\\d+.*)");
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
        //return string.matches("^\\d+$");
    }
    }


