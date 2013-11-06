package program5;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//TODO: Design and create a proper inplementation of the PubmedIDQuery class so it becomes part of TEA.
public class PubmedIDQuery {

    private String url;
    private String fileName;
    private String workspace;
    private ArrayList<String> pubmedIDs;

    public PubmedIDQuery(String query, String workspace, String maxPubmedIDs) throws IOException {
        this.url = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term="+query+"&datetype=pdat&maxdate=2010&mindate=1990&retmax="+maxPubmedIDs;
        this.fileName  = query+".xml";
        this.pubmedIDs = new ArrayList<String>();
        this.workspace = workspace;

    }
    public ArrayList<String> getPubmedIDs() throws IOException, InterruptedException {
        //wait(1000);
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
            File fileDir = new File(workspace);
            fileDir.mkdirs();
            savedFile = new File(workspace, fileName);
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

        fis = new FileInputStream(workspace+"/"+fileName);
        reader = new BufferedReader(new InputStreamReader(fis));

        System.out.println("Reading File line by line using BufferedReader");

        String line = reader.readLine();
        while(line != null){
            if (line.substring(0, 4).equals("<Id>")) {
                Pattern p = Pattern.compile("-?\\d+");
                Matcher m = p.matcher(line);
                while (m.find()) {
                    System.out.println(m.group());
                    pubmedIDs.add(m.group());
                }
            }
            line = reader.readLine();
        }
        reader.close();
        fis.close();
        return pubmedIDs;
    }

}

