package loft.program;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.Before;
import org.junit.Test;
import program.HeaderMethods;
import program.Kinetics;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * Created for project: TableExtraction
 * In package: program
 * Created with IntelliJ IDEA.
 * User: Sander van Boom
 * Date: 9-10-13
 * Time: 14:12

public class KineticsTest {
    private Collection<Kinetics> Kheaders;
    private List<String> names;
    @Before
    public void initialiser(){
        Gson gson = new Gson();
        Type collectionType2 = new TypeToken<Collection<Kinetics>>(){}.getType();
        names = new ArrayList<String>();
        try{
            Reader reader;
            reader = new InputStreamReader(HeaderMethods.class.getResourceAsStream("/program/enzymeKinetics.json"), "UTF-8");
            this.Kheaders = gson.fromJson(reader, collectionType2);
            for(Kinetics k: Kheaders){
                names.add(k.getName());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getNameTest(){
        String name = names.get(0);
        assertEquals("Vmax/Km", name);
    }

}
*/