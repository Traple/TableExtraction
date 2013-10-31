package program5;

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
    public static boolean isNumber(String string) {
        Pattern pattern = Pattern.compile("^(\\d+.*|-\\d+.*)");
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
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
