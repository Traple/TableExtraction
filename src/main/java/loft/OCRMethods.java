package loft;

import java.util.ArrayList;

/**
 * Created for project: TableExtraction
 * In package: loft
 * Created with IntelliJ IDEA.
 * User: Sander van Boom
 * Date: 10-10-13
 * Time: 17:11
 */
public class OCRMethods {
    /*            if(positions!=null){
            for(int a = 0; a<positions.length;a++){
            Matcher m = p.matcher(positions[a]);
                int counter = 0;
                while (m.find()) {
                counter ++;
                locations[counter] = Double.parseDouble(m.group());
            } }
            Point2D point1 = new Point2D.Double(locations[1], locations[2]);
            Point2D point2 = new Point2D.Double(locations[1], locations[2]);
            double distance = point1.distance(point2);
            System.out.println(distance);
            positions = null;
            }      */


/*
    public void getInfo() throws IOException {
        File input = new File("C:\\Users\\Sander van Boom\\Dropbox\\Tables and Figures\\OCR\\31-2.html");
        Document doc = Jsoup.parse(input, "UTF-8", "http://example.com/");

        Element link = doc.select("span.ocr_line").first();
        Elements spans = doc.select("span.ocrx_word");

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
                       }
                        int count =0;
                        for(Purification p : Pheaders){
                            //System.out.println(count);
                            if(p.getUnits().length == 0){

                            }
                            else if(word.equals(p.getUnits()[0])){
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
 */

    /*
            for(ArrayList<String> column : columnsMatrix){
            header = column.get(0);
            String type = "";
            for(String word : column){
                for(Purification p : Pheaders){
                    ArrayList<String> syns = new ArrayList<String>();
                    String synType = "";
                    for(int i = 0;i<p.getSynonyms().length;i++){
                        syns.add(p.getSynonyms()[i]);
                        synType = p.getTypes()[0];
                    }
                    try{
                        if(syns.contains(header)){
                            type = synType;

                        }
                    }
                    catch(ArrayIndexOutOfBoundsException e){

                    }
                }

            }
            if(type == ""){     //header wasn't found

            }
            else{
                System.out.println("HEADER: "+ header + ", TYPE: " + type);
                refinedColumnsContentMatrix.add(column);
            }
        }
     */
    /*

    public ArrayList<ArrayList<String>> transposeMatrix(ArrayList<ArrayList<String>> matrix){
        ArrayList<ArrayList<String>> transposedMatrix = new ArrayList<ArrayList<String>>();

        //String field = "";
        for(ArrayList<String> line : matrix){
            ArrayList<String> transposedLine = new ArrayList<String>();

        }

        return transposedMatrix;
    }
     */
}
