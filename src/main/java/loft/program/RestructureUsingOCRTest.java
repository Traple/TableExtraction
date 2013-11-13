package loft.program;

/**
 * Created for project: TableExtraction
 * In package: program
 * Created with IntelliJ IDEA.
 * User: Sander van Boom
 * Date: 9-10-13
 * Time: 15:04


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.Before;
import org.junit.Test;
import program.HeaderMethods;
import program.Purification;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import static junit.framework.Assert.assertEquals;

public class RestructureUsingOCRTest {
    public ArrayList<ArrayList<String>> matrix = new ArrayList<ArrayList<String>>();
    public ArrayList<String> headers = new ArrayList<String>();
    private Collection<Purification> Pheaders;

    @Before
    public void initialiser() throws UnsupportedEncodingException {
        ArrayList<String> column1 = new ArrayList<String>();
        column1.add("header");
        column1.add("1");
        column1.add("6.3");
        column1.add("100");
        ArrayList<String> column2 = new ArrayList<String>();
        column2.add("header2");
        column2.add("5");
        column2.add("17.8");
        column2.add("60");
        ArrayList<String> column3 = new ArrayList<String>();
        column3.add("header3");
        column3.add("10");
        column3.add("4.2");
        column3.add("30");
        this.matrix.add(column1);
        this.matrix.add(column2);
        this.matrix.add(column3);
        this.headers.add("step");
        this.headers.add("yield");
        this.headers.add("fold");
        this.headers.add("Yield");
        Gson gson = new Gson();
        Type collectionType2 = new TypeToken<Collection<Purification>>(){}.getType();


        Reader reader;
        reader = new InputStreamReader(HeaderMethods.class.getResourceAsStream("/program/purification.json"), "UTF-8");
        this.Pheaders = gson.fromJson(reader, collectionType2);

    }

    @Test
    public void transpose() {
        ArrayList<ArrayList<String>> trans = new ArrayList<ArrayList<String>>();
        int N = matrix.get(0).size();
        for (int i = 0; i < N; i++) {
            ArrayList<String> col = new ArrayList<String>();
            for (ArrayList<String> row : matrix) {
                col.add(row.get(i));
            }
            trans.add(col);
        }
        assertEquals("[[header, header2, header3], [1, 5, 10], [6.3, 17.8, 4.2], [100, 60, 30]]", trans.toString());
    }
/*
    @Test
    public void findHeaderTypes(){
        ArrayList<String> headerTypes = new ArrayList<String>();
        for(String headerInput : headers){
            ArrayList<String> syns = new ArrayList<String>();
            String synType = "";
            Purification p = new Purification();
            for(int i = 0;i<p.getSynonyms().length;i++){
                syns.add(p.getSynonyms()[i]);
                synType = p.getTypes()[0];
            }
            if(syns.contains(headerInput)){
                headerTypes.add(synType);
            }
        }
        assertEquals("[[header, header2, header3], [1, 5, 10], [6.3, 17.8, 4.2], [100, 60, 30]]", headerTypes.toString());
    }

}
*/