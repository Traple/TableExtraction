package program6;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//This class calls the NCBI API efetch to get pubmedID's.
public class PubmedIDQuery {

    private String url;
    private String fileName;
    private String workspace;
    private ArrayList<String> pubmedIDs;

    public PubmedIDQuery(String query, String workspace, String maxPubmedIDs) throws IOException, InterruptedException {
        this.url = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term="+query+"&datetype=pdat&maxdate=2010&mindate=1990&retmax="+maxPubmedIDs;
        this.fileName  = query+".xml";
        this.pubmedIDs = setPubmedIDs();
        this.workspace = workspace;

    }
    //~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-
    //Set methods of this class:

    /**
     * This method extracts the pubmedID's using the local variables as defined by the constructor.
     * @return an ArrayList with the pubmedID's that were extracted from pubmed.
     * @throws IOException
     * @throws InterruptedException
     */
    public ArrayList<String> setPubmedIDs() throws IOException, InterruptedException {
        try
        {
            URL fileURL = new URL(url);
            URLConnection connection = fileURL.openConnection();
            connection.connect();

            BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());
            File fileDir = new File(workspace);
            fileDir.mkdirs();
            File savedFile = new File(workspace, fileName);
            OutputStream out = new FileOutputStream(savedFile);

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

        FileInputStream fis = new FileInputStream(workspace+"/"+fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
        ArrayList<String> pubmedIDs = new ArrayList<String>();

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

    //~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-
    //Get methods of this class:

    public ArrayList<String> getPubmedIDs(){
        return pubmedIDs;
    }
}

