package program;

/**
 * Created for project: TableExtraction
 * In package: program
 * Created with IntelliJ IDEA.
 * User: Sander van Boom
 * Date: 9-10-13
 * Time: 12:06
 */
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/*
 * This class provides methods for detecting headers.
 */

public class HeaderMethods {
    private Set<String> purificationHeaders = new HashSet<String>();
    private Set<String> kineticHeaders = new HashSet<String>();
    private Set<String> activityHeaders = new HashSet<String>();
    private Collection<Purification> Pheaders;
    private Collection<Kinetics> Kheaders;
    private Collection<RelativeActivity> Aheaders;

    public HeaderMethods(){

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Purification>>(){}.getType();
        Type collectionType2 = new TypeToken<Collection<Kinetics>>(){}.getType();
        Type collectionType3 = new TypeToken<Collection<RelativeActivity>>(){}.getType();

        try{
            Reader reader;
            reader = new InputStreamReader(HeaderMethods.class.getResourceAsStream("/program/purification.json"), "UTF-8");
            this.Pheaders = gson.fromJson(reader, collectionType);
            for(Purification p: Pheaders){
                for(int u =0; u<p.getSynonyms().length;u++){
                    this.purificationHeaders.add(p.getSynonyms()[u]);
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try{
            Reader reader = new InputStreamReader(HeaderMethods.class.getResourceAsStream("/program/enzymeKinetics.json"), "UTF-8") ;
            this.Kheaders = gson.fromJson(reader, collectionType2);
            for(Kinetics k: Kheaders){
                for(int u =0; u<k.getSynonyms().length;u++){
                    this.kineticHeaders.add(k.getSynonyms()[u]);
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try{
            Reader reader = new InputStreamReader(HeaderMethods.class.getResourceAsStream("/program/relativeActivity.json"), "UTF-8");
            this.Aheaders = gson.fromJson(reader, collectionType3);
            for(RelativeActivity a: Aheaders){
                for(int u =0; u<a.getSynonyms().length;u++){
                    this.activityHeaders.add(a.getSynonyms()[u]);
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean containsHeaders(String cell){
        if(purificationHeaders.contains(cell)){
            System.out.println("HEADER: " + cell);
            return true;
        }
        else{
            for(String header:purificationHeaders){
                if(cell.contains(header)){
                    System.out.println("HEADER: " + header);
                    return true;
                }
            }}
        if(kineticHeaders.contains(cell)){
            System.out.println("HEADER: " + cell);
            return true;
        }
        else{
            for(String header:kineticHeaders){
                if(cell.contains(header)){
                    System.out.println("HEADER: " + header);
                    return true;
                }
            }}
        if(activityHeaders.contains(cell)){
            System.out.println("HEADER: " + cell);
            return true;
        }
        else{
            for(String header:activityHeaders){
                if(cell.contains(header)){
                    System.out.println("HEADER: " + header);
                    return true;
                }
            }}
        return false;
    }

    public String returnHeaders(String cell){
        if(purificationHeaders.contains(cell)){
            System.out.println("HEADER: " + cell);
            return cell;
        }
        else{
            for(String header:purificationHeaders){
                if(cell.contains(header)){
                    System.out.println("HEADER: " +header);
                    return header;
                }
            }
        }
        if(kineticHeaders.contains(cell)){
            System.out.println("HEADER: " + cell);
            return cell;
        }
        else{
            for(String header:kineticHeaders){
                if(cell.contains(header)){
                    System.out.println("HEADER: " +header);
                    return header;
                }
            }}
        if(activityHeaders.contains(cell)){
            System.out.println("HEADER: " + cell);
            return cell;
        }
        else{
            for(String header:activityHeaders){
                if(cell.contains(header)){
                    System.out.println("HEADER: " +header);
                    return header;
                }
            }
        }
        return "";
    }

    /*
     * The method setHeaderTypes matches the found headers against their type to provide a headerTypes ArrayList.
     */
    public String setHeaderType (String header){
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

        return type;
    }

    /*
     * The method linkHeadersToData links the information in the headers to the actual data.
     * If the type of the data does not match the header then it tries the next cell and see if it fits.
     * This is only done when the length of line > then the length of headers.
     */
    public String[] linkHeadersToData(ArrayList<String> headers, ArrayList<String> headerTypes, String[] line){
        String [] lineCheckedForData = line;
        if(headers.size() != headerTypes.size()){
            System.out.println("LENGTH IS NOT THE SAME. THERE IS SOMETHING WRONG!");
        }
        System.out.println(line.length);
        if(headers.size() == 0){
            System.out.println("the size is : " + headers.size()+ " nothing can be done without headers");
            return null;}

        else if(headers.size() < line.length){
            for(int x = 0; x<headers.size(); x++){
                if(line[x] != headers.get(x)){
                    System.out.println("From this point on we know that something went wrong.");


                    //TODO: Implement OCR and finnish this method.
                    return line;
                }
            }
        }
        else{
            return lineCheckedForData;
        }
        return null;
    }
}

