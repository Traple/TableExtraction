package program8;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * The class CommonMethods contains methods that are usually very simple and small. These methods can be used for a wide
 * range of classes/Projects. Reuse is key!
 */
public class CommonMethods {
    /**
     * This method calculates the distance between two points (coordinates).
     * @param x1 Point one
     * @param x2 Point two
     * @return The distance between point one and point two.
     */
    public static double calcDistance(double x1, double x2){
        double distance = 0.0;
        if(x1 > x2){
            distance = x1 - x2;
        }
        else if(x2 > x1){
            distance = x2 - x1;
        }
        return distance;
    }
    public static int calcDistance(int x1, int x2){
        int distance = 0;
        if(x1 > x2){
            distance = x1 - x2;
        }
        else if(x2 > x1){
            distance = x2 - x1;
        }
        return distance;
    }

    /**
     * This method calculates the average of a given list of integers.
     * @param list A list of integers.
     * @return The average of the list of integers.
     */
    public static double average(ArrayList<Integer> list)
    {
        double total = 0.0;
        for (Integer aList : list) {
            total += aList;
        }
        return total/list.size();
    }

    @SuppressWarnings("UnusedDeclaration")          //For future use.
    public static boolean containsNumber(String string) {
        Pattern pattern = Pattern.compile("^(\\d+.*|-\\d+.*)");
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }
    //for example: if convert to 1 () creates an int, convert to 1 and call it an int.

    /**
     * This method checks if the given string is in fact a number. This includes changing some basic OCR mistakes like
     * converting a 1 to a i.
     * @param string A string potential being a number.
     * @return true if the string only contains numbers or characters that are likely to be changed by the OCR.
     */
    public static boolean isNumber(String string){
        int numbers = 0;
        int strings = 0;
        boolean isNumber = false;
        char[] characters = string.toCharArray();
        for(char character : characters){
            try{
                if(String.valueOf(character).equals("i")||String.valueOf(character).equals("I")||String.valueOf(character).equals("l")){
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

    @SuppressWarnings("UnusedDeclaration")              //For future use.
    public static ArrayList<Double> makeListPositive(ArrayList<Double> negativeList){
        ArrayList<Double> positiveList = new ArrayList<Double>();
        for(double currentNumber : negativeList){
            positiveList.add(Math.abs(currentNumber));
        }
        return positiveList;
    }

    @SuppressWarnings("UnusedDeclaration")              //For future use.
    public static ArrayList<Double> addNumberToList(ArrayList<Double> list, double number){
        ArrayList<Double> newList = new ArrayList<Double>();
        for(Double currentNumber : list){
            newList.add(currentNumber + number);
        }
        return newList;
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

    /**
     * This method returns the most common integer in a given list.
     * @param list A list containing integers.
     * @return The most common integer in the list.
     */
    public static Integer mostCommonElement(List<Integer> list) {
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();

        for (Integer aList : list) {
            Integer frequency = map.get(aList);
            if (frequency == null) {
                map.put(aList, 1);
            } else {
                map.put(aList, frequency + 1);
            }
        }
        Integer mostCommonKey = null;
        int maxValue = -1;
        for(Map.Entry<Integer, Integer> entry: map.entrySet()) {
            if(entry.getValue() > maxValue) {
                mostCommonKey = entry.getKey();
                maxValue = entry.getValue();
            }
        }
        return mostCommonKey;
    }

    /**
     * This method changes illegal XML characters and replaces them with non illegal characters.
     * @param input A string
     * @return A string containing the input without illegal XML characters.
     */
    public static String changeIllegalXMLCharacters(String input){
        if(input.contains("<")){
            input = input.replace("<", "&lt;");
        }
        if(input.contains(">")){
            input = input.replace(">", "&gt;");
        }
        if(input.contains("&")){
            input = input.replace("&","&amp;");
        }
        if(input.contains("'")){
            input = input.replace("'", "&apos;");
        }
        if(input.contains("\"")){
            input = input.replace("\"", "&quot;");
        }
        return input;
    }
}