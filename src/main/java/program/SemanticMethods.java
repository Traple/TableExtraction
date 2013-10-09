package program;

/**
 * Created for project: TableExtraction
 * In package: program
 * Created with IntelliJ IDEA.
 * User: Sander van Boom
 * Date: 9-10-13
 * Time: 12:27
 */
import java.util.ArrayList;

/*
 * This class contains methods for the use of semantics in tables.
 * In other words, it contains methods that link all the findings (headers, data patterns, etc.).
 */
public class SemanticMethods{

	/*
	 * This method takes an ArrayList with the headers and a row. Then it links the headers to the rows and prints the results
	 */

    public void printTable(ArrayList headers, String [] row){
        System.out.println("Number of headers: " + headers.size());
        System.out.println("Headers:\t\tValues:");
        for(int x =0;x<headers.size();x++){
            System.out.print(headers.get(x)+"\t"+"\t"+"\t");
            try{
                System.out.println(row[x]);
            }
            catch (ArrayIndexOutOfBoundsException e){
                System.out.println("UNKNOWN");
            }
        }
    }

}
