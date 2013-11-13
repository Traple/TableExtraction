package program6;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * The class CommonMethods contains methods that are usualy very simple and small. These methods can be used for a wide
 * range of classes/Projects. Reuse is key!
 */
public class CommonMethods {

    @SuppressWarnings("UnusedDeclaration")
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
    public int calcDistance(int x1, int x2){
        int distance = 0;
        if(x1 > x2){
            distance = x1 - x2;
        }
        else if(x2 > x1){
            distance = x2 - x1;
        }
        return distance;
    }

    @SuppressWarnings("UnusedDeclaration")
    public static boolean containsNumber(String string) {
        Pattern pattern = Pattern.compile("^(\\d+.*|-\\d+.*)");
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }

    //TODO: Create more rules for this: A lot of times a cell is seen as a string (26-4)
    //for example: if convert to 1 () creates an int, convert to 1 and call it an int.
    public static boolean isNumber(String string){
        int numbers = 0;
        int strings = 0;
        boolean isNumber = false;
        char[] characters = string.toCharArray();
        for(char character : characters){
            try{
                if(String.valueOf(character) == "i"){
                    character = "1".charAt(0);
                }
                Integer.parseInt(String.valueOf(character));
                numbers += 1;
            }
            catch(NumberFormatException e){
                strings += 1;
            }
        }
        if(numbers> strings){
            isNumber = true;
        }
        return isNumber;
    }

    @SuppressWarnings("UnusedDeclaration")
    public int lowestNumber(int number1, int number2){
        int lowestNumber = number1;
        if(number1 > number2){
            lowestNumber = number2;
        }
        if(number2 > number1){
            lowestNumber = number1;
        }
        return lowestNumber;
    }

    @SuppressWarnings("UnusedDeclaration")
    public double lowestNumber(double number1, double number2){
        double lowestNumber = number1;
        if(number1 > number2){
            lowestNumber = number2;
        }
        if(number2 > number1){
            lowestNumber = number1;
        }
        return lowestNumber;
    }
}
