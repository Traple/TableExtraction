package loft.program;

/**
 * Created for project: TableExtraction
 * In package: program
 * Created with IntelliJ IDEA.
 * User: Sander van Boom
 * Date: 9-10-13
 * Time: 15:17

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import uk.ac.cam.ch.wwmm.oscar.Oscar;
import uk.ac.cam.ch.wwmm.oscar.chemnamedict.entities.ResolvedNamedEntity;
import uk.ac.cam.ch.wwmm.oscar.document.NamedEntity;
import uk.ac.cam.ch.wwmm.oscar.types.NamedEntityType;

import static junit.framework.Assert.assertEquals;

public class MetaboliteDatabaseTest {
    private String possibleMetabolite;

    @Before
    public void initialiser(){
        possibleMetabolite = "hydrochlorate";
    }

    @Test
    public void checkForMetabolitesTest(){
        String s = possibleMetabolite;
        boolean result = false;
        Oscar oscar = new Oscar();
        List<NamedEntity> entities = oscar.findNamedEntities(s);
        for (NamedEntity ne : entities) {
            if(ne.getType()==NamedEntityType.COMPOUND){
                System.out.println(ne.getSurface());
                result = true;
            }
            else{
            System.out.println("Not a molecule but something chemical. This is a: "+ne.getType().toString());
            result = false;}
        }
        assertEquals(true, result);
    }
}
*/