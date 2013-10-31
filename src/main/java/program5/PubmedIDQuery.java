package program5;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//This is a test for TEA 0.5!!
//Hidden feature, so secret!
//TODO: Design and create a proper inplementation of the PubmedIDQuery class so it becomes part of TEA.
public class PubmedIDQuery {
    public static void main(String[] args) throws IOException {
        String query = "tca cycle";
        String url = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pmc&term="+query+"&datetype=pdat&maxdate=2010&mindate=1990";
        String pathToSave = "C:\\Users\\Sander van Boom\\Dropbox\\Tables and Figures\\T.E.A. Version Information\\T.E.A. 0.4\\TestFileFromWorkflow";
        String fileName  = "influenza.xml";
        ArrayList<String> pmcIDs = new ArrayList<String>();
        ArrayList<String> pubmedIDs = new ArrayList<String>();

        BufferedInputStream inputStream = null;
        OutputStream out = null;

        File savedFile = null;

        try
        {
            // Replace your URL here.
            URL fileURL = new URL(url);
            URLConnection connection = fileURL.openConnection();
            connection.connect();

            inputStream = new BufferedInputStream(connection.getInputStream());

            // Replace your save path here.
            File fileDir = new File(fileName);
            fileDir.mkdirs();
            savedFile = new File(pathToSave, fileName);
            out = new FileOutputStream(savedFile);

            byte buf[] = new byte[1024];
            int len;

            long total = 0;

            while ((len = inputStream.read(buf)) != -1)
            {
                total += len;
                out.write(buf, 0, len);
            }


}catch(IOException e){
        System.out.println(e);
        }

        FileInputStream fis = null;
        BufferedReader reader = null;

        fis = new FileInputStream(pathToSave+"\\"+fileName);
        reader = new BufferedReader(new InputStreamReader(fis));

        System.out.println("Reading File line by line using BufferedReader");

        String line = reader.readLine();
        while(line != null){
            if (line.substring(0, 4).equals("<Id>")) {
                Pattern p = Pattern.compile("-?\\d+");
                Matcher m = p.matcher(line);
                while (m.find()) {
                    System.out.println(m.group());
                    pmcIDs.add(m.group());
                }
            }
            line = reader.readLine();
        }
        reader.close();
        fis.close();

        //_-------------------------------------------------------------------
        for(String pmcID : pmcIDs){
            String url2 = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/elink.fcgi?dbfrom=pmc&id="+pmcID+"&db=pubmed";
            String fileName2  = "influenzaPMCSearch.xml";

            try
            {
                // Replace your URL here.
                URL fileURL = new URL(url2);
                URLConnection connection = fileURL.openConnection();
                connection.connect();

                inputStream = new BufferedInputStream(connection.getInputStream());

                // Replace your save path here.
                File fileDir = new File(fileName2);
                fileDir.mkdirs();
                savedFile = new File(pathToSave, fileName2);
                out = new FileOutputStream(savedFile);

                byte buf[] = new byte[1024];
                int len;

                long total = 0;

                while ((len = inputStream.read(buf)) != -1)
                {
                    total += len;
                    out.write(buf, 0, len);
                }


            }catch(IOException e){
                System.out.println(e);
            }

            //----------------------------------------------------------------

            FileInputStream fis2 = null;
            BufferedReader reader2 = null;

            fis2 = new FileInputStream(pathToSave+"\\"+fileName2);
            reader2 = new BufferedReader(new InputStreamReader(fis2));

            String line2 = reader2.readLine();
            while(line2 != null){

                try{
                if (line2.contains("<Id>")) {
                    System.out.println(line2);
                    Pattern p = Pattern.compile("-?\\d+");
                    Matcher m = p.matcher(line);
                    while (m.find()) {
                        System.out.println("Pubmed: "+m.group());
                        pubmedIDs.add(m.group());
                        break;
                    }
                }
                }
                catch(NullPointerException e){

                }
                line2 = reader2.readLine();
            }
            reader2.close();
            fis2.close();
        }

    }


}

