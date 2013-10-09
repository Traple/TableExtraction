package program;

/**
 * Created for project: TableExtraction
 * In package: program
 * Created with IntelliJ IDEA.
 * User: Sander van Boom
 * Date: 9-10-13
 * Time: 12:12
 */
import java.util.ArrayList;
import java.util.List;

import uk.ac.cam.ch.wwmm.oscar.Oscar;
import uk.ac.cam.ch.wwmm.oscar.chemnamedict.entities.ChemicalStructure;
import uk.ac.cam.ch.wwmm.oscar.chemnamedict.entities.FormatType;
import uk.ac.cam.ch.wwmm.oscar.chemnamedict.entities.ResolvedNamedEntity;
import uk.ac.cam.ch.wwmm.oscar.types.NamedEntityType;

/*
Chemistry means the difference between poverty and starvation and the abundant life.
- Brent, Robert
*/
public class MetaboliteDatabase {

    public boolean checkForMetabolites(String possibleMetabolite){
        String s = possibleMetabolite;
        boolean result = false;
        Oscar oscar = new Oscar();
        List<ResolvedNamedEntity> entities = oscar.findAndResolveNamedEntities(s);
        for (ResolvedNamedEntity ne : entities) {
            if(ne.getType().toString()=="CM"){
                System.out.println(ne.getSurface());
                result = true;
            }
            System.out.println("Not a molecule but something chemical. This is a: "+ne.getType().toString());
            result = false;
        }
        return result;
    }
    public ArrayList replaceStringsWithMetabolites(ArrayList pattern, String[] row){
        String stringToBeChecked = "";
        for(int x = 0; x<pattern.size();x++){
            if(pattern.get(x) == "S"){
                stringToBeChecked = row[x].toString();
                if(checkForMetabolites(stringToBeChecked)){
                    pattern.set(x, "M");
                    System.out.println("This is a metabolite: " + row[x]);
                }}}
        return pattern;
    }

}
