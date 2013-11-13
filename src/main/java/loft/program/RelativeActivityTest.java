package loft.program;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.Before;
import org.junit.Test;
import program.HeaderMethods;
import program.RelativeActivity;

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

public class RelativeActivityTest {
    private Collection<RelativeActivity> Aheaders;
    private List<String> names;
    private List<String[]> synonyms;
    private List<String[]> types;
    @Before
    public void initialiser() throws UnsupportedEncodingException {
        Gson gson = new Gson();
        Type collectionType2 = new TypeToken<Collection<RelativeActivity>>(){}.getType();
        names = new ArrayList<String>();
        synonyms = new ArrayList<String[]>();
        types = new ArrayList<String[]>();
            Reader reader;
            reader = new InputStreamReader(HeaderMethods.class.getResourceAsStream("/program/relativeActivity.json"), "UTF-8");
            this.Aheaders = gson.fromJson(reader, collectionType2);
            for(RelativeActivity a: Aheaders){
                names.add(a.getName());
                synonyms.add(a.getSynonyms());
                types.add(a.getTypes());
            }
    }

    @Test
    public void getNameTest(){
        String name = names.get(0);
        assertEquals("substrate", name);
    }
    @Test
    public void getSynonymsTest(){
        String name = synonyms.get(0)[0];
        assertEquals("substrate", name);
    }
    @Test
    public void getTypeTest(){
        String name = types.get(0)[0];
        assertEquals("S", name);
    }
}
*/