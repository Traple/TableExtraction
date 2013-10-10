package program;

/**
 * Created for project: TableExtraction
 * In package: program
 * Created with IntelliJ IDEA.
 * User: Sander van Boom
 * Date: 9-10-13
 * Time: 12:08
 */

public class Purification {
    private String name;
    private String[] synonyms;
    private String[] types;
    private String[] units;

    // Getters and setters are not required for this example.
    // GSON sets the fields directly using reflection.
    public Purification(){

    }
    public String getName() {
        return name;
    }
    public String[] getSynonyms(){
        return synonyms;
    }
    public String[] getTypes(){
        return types;
    }
    public String[] getUnits(){
        return units;
    }

}
