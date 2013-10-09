package program;

/**
 * Created for project: TableExtraction
 * In package: program
 * Created with IntelliJ IDEA.
 * User: Sander van Boom
 * Date: 9-10-13
 * Time: 12:11
 */

public class Kinetics {
    private String name;
    private String[] synonyms;
    private String[] types;

    // Getters and setters are not required for this example.
    // GSON sets the fields directly using reflection.
    public Kinetics(){

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

}
