package program; /**
 * Created with IntelliJ IDEA.
 * User: Sander van Boom
 * Date: 9-10-13
 * Time: 11:37
 * To change this template use File | Settings | File Templates.
 */

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import au.com.bytecode.opencsv.CSVReader;

public class Main {
    public static Logger LOGGER = Logger.getLogger(Main.class.getName());
    public static void main(String[] args) {
        //csv file containing data

        String strFile = "C:\\Users\\Sander van Boom\\Dropbox\\Tables and Figures\\Corpus 1.1\\CorpusParamMain\\31.pdf.csv";
        LOGGER.info("Read file: "+ strFile);
        try {
            RestructureUsingOCR OCR = new RestructureUsingOCR();
            //OCR.lineChecker(4337, 146, "yield");
            ArrayList mahList = new ArrayList();
            ArrayList<String> muhList = new ArrayList<String>();
            mahList = OCR.lineChecker(4187, 200, "yielding");
            System.out.println(mahList.size());
            muhList = OCR.evalueteColumn(mahList);
            System.out.println(muhList.size());
            for(String muh: muhList){
                System.out.println(muh);
            }
            //OCR.getInfo();
            System.exit(0);
            read(strFile);

	    	/*
	    	  CSVReader reader = new CSVReader(new FileReader(strFile));
	    	  int counter = 0;
	    	  while(true){
	    		  counter++;
	    		  String[] line = reader.readNext();
	    		  try{
	    		  System.out.println(counter +" "+ line[0]);
	    		  }
	    		  catch(NullPointerException e){


	    		  }
	    				  if(line == null){
	    			  break;
	    		  }
	    	  }
	    	  */

	    	/*
	    	Scanner scan = new Scanner(new BufferedReader(new FileReader(new File(strFile))));
	    	int counter =0;
	    	while(scan.hasNextLine()){
	    		counter++;
	    		String line = scan.nextLine();
	    		System.out.println(line);
	    		System.out.println(counter);
	    	}
	    	scan.close();
	    	*/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //the read method reads the chosen file and parses them to other classes if it detecets a table.
    public static void read(String fileLocation ) throws IOException{
        CSVReader reader = new CSVReader(new FileReader(fileLocation));
        String[] nextLine;
        int lineNumber = 0;
        ArrayList currentRow;
        String[] lastRow = null;
        ArrayList lastRowPattern = new ArrayList<ArrayList<String>>();
        RestructureUsingNLP NLP = new RestructureUsingNLP();
        MetaboliteDatabase MD = new MetaboliteDatabase();
        ArrayList tableContent = new ArrayList();
        boolean tableCeption = false;
        HeaderMethods HM = new HeaderMethods();


        while ((nextLine = reader.readNext()) != null) {
            lineNumber++;
            //System.out.println(lineNumber);
            //System.out.println(nextLine[0]);
            String currentline = nextLine[0].toString(); // nextLine[] is an array of values from the line
            //A table is detected if the word Table or TABLE or table is found in the first cell.
            //System.out.println(currentline);
            try{
                String possibleTable = currentline.substring(0, 5);
                //System.out.println(possibleTable);
                //System.out.println(lineNumber);
                if(possibleTable.contains("Table")||possibleTable.contains("TABLE")||possibleTable.contains("table")||tableCeption){
                    LOGGER.info("Found a possible table because of: " + currentline + " in the first cell.");
                    //table:
                    //System.out.println("table: " + currentline);
                    tableCeption = false;

                    //Now we give every table 20 lines to find a pattern. As long as the pattern keeps going
                    //the time won't change.
                    int timer=0;		//every table gets 20 lines to start with
                    int pattern =0;		//and starts without a set pattern
                    int patternScore =0;	//the score of each pattern, score is increased by numerical data and metabolites.
                    //ArrayList tableContent = new ArrayList();
                    ArrayList readLines = new ArrayList<String[]>();
                    int linesReadInTable = 0;
                    int startOfPattern =0;
                    ArrayList<String[]> tableValues = new ArrayList<String[]>();

                    //Found a new table:
                    while(timer != 20){
                        //System.out.println(nextLine[0]);
                        nextLine = reader.readNext();
                        if(nextLine != null){
                            lineNumber++;
                            //System.out.println(timer);
                            currentRow = NLP.evaluateSentence(nextLine);

                            linesReadInTable++;
                            readLines.add(nextLine);


                            //if you want to check for metabolites:
                            //	if(pattern>0){
                            //		MD.replaceStringsWithMetabolites(currentRow, nextLine);
                            //	}
                            //System.out.println(NLP.twoSentencePattern(currentRow, lastRowPattern));
                            if(NLP.twoSentencePattern(currentRow, lastRowPattern)&&nextLine.length>1){

                                //if you want to check for metabolites:
                                //		if(currentRow.contains("S")){
                                //			MD.replaceStringsWithMetabolites(currentRow, nextLine);
                                //			MD.replaceStringsWithMetabolites(lastRow, nextLine);
                                //		}



                                //if the pattern is starting then we need to add the first one of the pattern as well.
                                if(pattern == 0){
                                    System.out.println(lastRow[0]);
                                    tableValues.add(lastRow);
                                    pattern++;
                                    startOfPattern = linesReadInTable;
                                    System.out.println("Start of pattern: " + startOfPattern);
                                }
                                pattern++;
                                //Now we print and score the pattern:
                                System.out.println(nextLine[0]);
                                tableValues.add(nextLine);
                                patternScore = patternScore + NLP.scorePattern(lastRowPattern);
                                System.out.println("pattern length: "+pattern);
                                System.out.println("PatternScore: " + patternScore);

                            }

                            else{
                                timer++;
                                //we want to break if we think we've read enough
                                //	System.out.println("nomatch, p: "+pattern);
                                //	System.out.println("nomatch, ps: "+patternScore);
                                if(pattern>=2&&patternScore>0){
                                    System.out.println("Exception! MAYBE NEXT LINE?");
                                    String[] breakingLine = nextLine;
                                    nextLine = reader.readNext();
                                    lineNumber++;
                                    currentRow = NLP.evaluateSentence(nextLine);
                                    if(nextLine != null && NLP.twoSentencePattern(currentRow, lastRowPattern)&&nextLine.length>1){
                                        pattern++;
                                        tableValues.add(breakingLine);
                                        tableValues.add(nextLine);
                                        timer--;
                                        System.out.println("Continue");
                                        System.out.println(breakingLine[0]);
                                        System.out.println(nextLine[0]);

                                    }

                                    else{
                                        String falseLine = breakingLine[0].toString();
                                        String falseLinePossibleTable = "";
                                        try{
                                            falseLinePossibleTable  = falseLine.substring(0, 5);}
                                        catch(StringIndexOutOfBoundsException siobe){
                                            System.out.println("no table over here.");
                                        }
                                        System.out.println(falseLinePossibleTable);
                                        if(falseLinePossibleTable.contains("table")||falseLinePossibleTable.contains("Table")||falseLinePossibleTable.contains("TABLE")){
                                            System.out.println("tableCeption!");
                                            tableCeption = true;
                                        }
                                        LOGGER.info("Pattern is broken.");
                                        System.out.println("The pattern was broken.");
                                        System.out.println("I think this was the pattern.");
                                        LOGGER.info("Found a pattern with pattern length: " + pattern + " with patternScore: " + patternScore);

                                        patternScore =0;
                                        pattern =0;
                                        System.out.println("Now evaluating pattern....");

                                        String[] lineRead;
                                        //Now we have to evaluate everything that has been read before the table and see if we can detect possible headers and make something of the content of the table.
                                        ArrayList<String> headers = new ArrayList<String>();
                                        ArrayList<String> headerTypes = new ArrayList<String>();
                                        for(int m = 0; m<startOfPattern;m++){
                                            lineRead = (String[]) readLines.get(m);
                                            //System.out.println("first: " + readLines.get(m));
                                            System.out.println(lineRead[0]);
                                            for(int n =0;n<lineRead.length;n++){
                                                System.out.println("reading: " + lineRead[n]);
                                                if(HM.containsHeaders(lineRead[n])){
                                                    LOGGER.info("Found a header: " + lineRead[n]);
                                                    //System.out.println("Found a Header!: "+ lineRead[n]);
                                                    headers.add(HM.returnHeaders(lineRead[n]));
                                                    headerTypes.add(HM.setHeaderType(lineRead[n]));

                                                }
                                            }
                                        }

                                        SemanticMethods SM = new SemanticMethods();
                                        String [] trueLine;
                                        for(int t = 0; t<tableValues.size();t++){
                                            trueLine = (HM.linkHeadersToData(headers, headerTypes, tableValues.get(t)));
                                            if(trueLine == null){
                                                System.out.println("no Headers have been found.");
                                                break;
                                            }
                                            else{
                                                SM.printTable(headers, trueLine);
                                                System.out.println("~-~-~-~-~-~-~-~-~-~-~-~-~-~-~");
                                            }}

                                        break;
                                    }
                                    continue;}


                            }

                            lastRowPattern = currentRow;
                            lastRow = nextLine;

                        }else{
                            break;}
                    }

                    //NLP.printPattern(possibleTableContent);
	        	/*
	        	line1 = nextLine.clone();
	      		nextLine = reader.readNext();
	      		lineNumber++;
	      		line2 = nextLine.clone();
	      		nextLine = reader.readNext();
	      		lineNumber++;
	      		line3 = nextLine.clone();
	      		restructure structure = new restructure(line1, line2, line3);

	      		restructureUsingNLP NLP = new restructureUsingNLP(line1);

	      		System.out.println("table: ");
	      		if(NLP.evaluateSentence(line1)){
	      			//do some semantics on line 1
	      			System.out.println("Semantics @ line 1");
	      			System.out.println("sentences: "+NLP.sentenceDetector(line2));
	      			System.out.println("sentences: "+NLP.sentenceDetector(line3));
	      		}
	      		else{
	      			if(NLP.evaluateSentence(line2)){
	      				//do some semantics on line 2
	      				System.out.println("Semantics @ line 2");
		      			System.out.println("sentences: "+NLP.sentenceDetector(line3));
	      			}
	      			else{
	      				if(NLP.evaluateSentence(line3)){
	      					//do some semantics on line 3
	      					System.out.println("Semantics @ line 3");
	      				}
	      				else{
	      					System.out.println("couldn't find the actual data =(");
	      				}
	      			}
	      		}
*/



                    //before we start rebuilding our table, we check if the lines given are just to small for it to be an actual table.
	      	/*
	      		if(structure.discardTable(line1, line2, line3)){
	      			System.out.println("table deleted");
	      		}
	      		else{
	      			structure.reconstructing(line1, line2, line3);
	      		}
	       */}
            }


            catch(StringIndexOutOfBoundsException siobe){

            }



        }reader.close();
    }

    //~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-
    //writing files:
	/*
	 * True glory consists in doing what deserves to be written and writing, what deserves to be read
	 * - Pliny the Elder, Roman scholar & scientist (23 AD - 79 AD)
	 *
	 * The write function takes the filecontent and a location, creates a new file and writes the content in that file.
	 */
    public static void write(String filecontent, String location){
        FileWriter fileWriter = null;
        try {
            File newTextFile = new File(location);
            fileWriter = new FileWriter(newTextFile);
            fileWriter.write(filecontent);
            fileWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fileWriter.close();
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        } }
}

