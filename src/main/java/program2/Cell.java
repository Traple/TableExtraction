package program2;

/**
 * Created for project: TableExtraction
 * In package: program2
 * Created with IntelliJ IDEA.
 * User: Sander van Boom
 * Date: 15-10-13
 * Time: 16:03
 */

/*
 * Every column has Cells, cells have content, positions and a type.
 */
public class Cell {
    private String content;
    private String positions;
    private String type;
    private CommonMethods CM;

    public Cell(String content, String positions){
        this.content = content;
        this.positions = positions;
        this.CM = new CommonMethods();
        this.type = findType();

    }

    public String findType(){
        //if the 1 has been changed to a I we need to fix that.
        if(CM.isNumber(content.replace("I", "1"))&&CM.isNumber(content)==false){
            this.content = content.replace("I", "1");
        }

        //then we can have a look at the type.
        if(CM.isNumber(content)){
        type = "N";
        }
        else{
             if(content.equals("ND")||content.equals("|")||content.contains(">")||content.contains("<")
                ||content.contains("N/D")||content.equals("-")||content=="NA"||content=="/"){
                type="N";
            }
            else{
            type = "S";
            }
        }
        return type;
    }

    public String getContent(){
        return content;
    }

    public String getPositions(){
        return positions;
    }

    public String getType(){
        return type;
    }
}
