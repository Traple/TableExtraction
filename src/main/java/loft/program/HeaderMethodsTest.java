/*
package loftTest.program;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.Before;
import org.junit.Test;
import program.HeaderMethods;
import program.Kinetics;
import program.Purification;
import program.RelativeActivity;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;

public class HeaderMethodsTest {
    private Collection<Purification> Pheaders;
    private Collection<Kinetics> Kheaders;
    private Collection<RelativeActivity> Aheaders;
    private String header;
    private Set<String> purificationHeaders = new HashSet<String>();
    private Set<String> kineticHeaders = new HashSet<String>();
    private Set<String> activityHeaders = new HashSet<String>();

    @Before
    public void initialiser() throws UnsupportedEncodingException {
        header = "substrate";

        //~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Purification>>(){}.getType();
        Type collectionType2 = new TypeToken<Collection<Kinetics>>(){}.getType();
        Type collectionType3 = new TypeToken<Collection<RelativeActivity>>(){}.getType();

            Reader reader;
            reader = new InputStreamReader(HeaderMethods.class.getResourceAsStream("/program/purification.json"), "UTF-8");
            this.Pheaders = gson.fromJson(reader, collectionType);
            for(Purification p: Pheaders){
                for(int u =0; u<p.getSynonyms().length;u++){
                    this.purificationHeaders.add(p.getSynonyms()[u]);
                }
            }
            reader = new InputStreamReader(HeaderMethods.class.getResourceAsStream("/program/enzymeKinetics.json"), "UTF-8") ;
            this.Kheaders = gson.fromJson(reader, collectionType2);
            for(Kinetics k: Kheaders){
                for(int u =0; u<k.getSynonyms().length;u++){
                    this.kineticHeaders.add(k.getSynonyms()[u]);
                }
            }
            reader = new InputStreamReader(HeaderMethods.class.getResourceAsStream("/program/relativeActivity.json"), "UTF-8");
            this.Aheaders = gson.fromJson(reader, collectionType3);
            for(RelativeActivity a: Aheaders){
                for(int u =0; u<a.getSynonyms().length;u++){
                    this.activityHeaders.add(a.getSynonyms()[u]);
                }
            }
    }

    @Test
    public void setHeaderTypeTest (){
        String type = "";
        for(Purification p: Pheaders){
            for(int u =0; u<p.getSynonyms().length;u++){
                if(header.contains(p.getSynonyms()[u])){
                    //get it's types:
                    type = p.getTypes()[0];
                    System.out.println("Muh Type:" + type);
                }
            }
        }
        if(type == ""){
            for(Kinetics k: Kheaders){
                for(int u =0; u<k.getSynonyms().length;u++){
                    if(header.contains(k.getSynonyms()[u])){
                        //get it's types:
                        type = k.getTypes()[0];
                        System.out.println("Muh Type:" + type);
                    }
                }
            }
        }
        if(type == ""){
            for(RelativeActivity a: Aheaders){
                for(int u =0; u<a.getSynonyms().length;u++){
                    if(header.contains(a.getSynonyms()[u])){
                        //get it's types:
                        type = a.getTypes()[0];
                        System.out.println("Muh Type:" + type);
                    }
                }
            }
        }

        assertEquals("S", type);
    }

}
                                   */