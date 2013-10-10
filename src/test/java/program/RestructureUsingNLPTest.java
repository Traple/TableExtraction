package program;

/**
 * Created for project: TableExtraction
 * In package: program
 * Created with IntelliJ IDEA.
 * User: Sander van Boom
 * Date: 9-10-13
 * Time: 15:04
 */

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

public class RestructureUsingNLPTest {
    private String[] line = {"crude extract"};
    private String stringy = "832894089283582492489";
    @Before
    public void initialiser(){

    }

    //TODO: create aditional test cases for evaluateSentence

    @Test
    public void evaluateSentence(){
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
        String test = pattern.get(0);
        assertEquals("S", test);

    }


    public static boolean isNumber(String string) {
        return string.matches("^\\d+$");
    }
@Test
public void isNumber() {
    boolean test = stringy.matches("^\\d+$");
    assertEquals(true, test);
}

}
