package program;

/**
 * Created for project: TableExtraction
 * In package: program
 * Created with IntelliJ IDEA.
 * User: Sander van Boom
 * Date: 9-10-13
 * Time: 15:04
 */

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class PurificationTest {
    private Collection<Purification> Pheaders;
    private List<String> names;

    @Before
    public void initialiser() throws UnsupportedEncodingException {
        Gson gson = new Gson();
        Type collectionType2 = new TypeToken<Collection<Purification>>(){}.getType();
        names = new ArrayList<String>();

            Reader reader;
            reader = new InputStreamReader(HeaderMethods.class.getResourceAsStream("/program/Purification.json"), "UTF-8");
            this.Pheaders = gson.fromJson(reader, collectionType2);
            for(Purification p: Pheaders){
                names.add(p.getName());
            }
    }

    @Test
    public void getNameTest(){
        String name = names.get(0);
        assertEquals("Step", name);
    }

}
