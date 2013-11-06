package program5;


import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class ArgumentProcessor {

    public static Logger LOGGER = Logger.getLogger(ArgumentProcessor.class.getName());
    private boolean help;
    private ArrayList<String> pubmedIDs;
    private String workspace;
    private boolean PDFFiles;
    private String query;
    private String pathToConfigFile;
    private String pathToImageMagick;
    private String pathToTesseract;
    private String pathToTesseractConfigFile;
    public ArgumentProcessor(String[] args) throws ParseException, IOException {
        CommandLineParser parser = new PosixParser();
        Options options = new Options();

        Option help = new Option("H", "Help",false ,"This is the help file of TEA.");
        Option optionPubmedIDs = new Option("PUB", "pubmedIDFile", true, "The file with the PubmedID's");
        Option optionWorkspace = new Option("W", "workspace", true, "The workspace for the program.");
        Option optionPDFFiles= new Option("PDF", "PDFFiles", false, "Instead of using a query or a pubmedID file, use the PDFs in the workspace.");
        Option optionQuery= new Option("QUE", "Query", true, "Use a given query to search pubmed and extract the articles.");
        Option optionConfig = new Option("C", "Config", true, "Specify the path to the configuration file.");

        options.addOption(help);
        options.addOption(optionPubmedIDs);
        options.addOption(optionWorkspace);
        options.addOption(optionPDFFiles);
        options.addOption(optionQuery);
        options.addOption(optionConfig);

        CommandLine line = parser.parse(options, args);

        this.help = setHelp(line);
        this.pubmedIDs = setPubmedIDs(line);
        this.workspace = setWorkspace(line);
        this.PDFFiles = setPDFFiles(line);
        this.query = setQuery(line);
        setPathToConfigFileValues(line);


    }

    //~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-

    private boolean setHelp(CommandLine line){
        if(line.hasOption("H")){
            return true;
        }
        else{
            return false;
        }
    }

    private ArrayList<String> setPubmedIDs(CommandLine line) throws IOException {
        String pubmedFile = "";
        if (line.hasOption("PUB")){
            pubmedFile = line.getOptionValue("PUB");
        }
        else{
            LOGGER.info("Couldn't find a PubmedID file.");
            return null;
        }

        //Open the file and read the PubmedIDs
        BufferedReader br = new BufferedReader(new FileReader(pubmedFile));
        String readline;
        ArrayList<String> pubmedIDs = new ArrayList<String>();
        while ((readline = br.readLine()) != null) {
            pubmedIDs.add(readline);
        }
        br.close();
        LOGGER.info("Found " + pubmedIDs.size() + " pubmedIDs from the PubmedID file.");
        return pubmedIDs;
    }

    private String setWorkspace(CommandLine line){
        String workspace = "";
        if(line.hasOption("W")){
            workspace = line.getOptionValue("W");
            return workspace;
        }
        else{
            return null;
        }
    }

    private boolean setPDFFiles(CommandLine line){
        return line.hasOption("PDF");
    }

    private String setQuery(CommandLine line){
        String Query = null;
        if(line.hasOption("QUE")){
            Query = line.getOptionValue("QUE");

        }
        return Query;
    }
    private void setPathToConfigFileValues(CommandLine line) throws IOException {
        if(line.hasOption("C")){
            pathToConfigFile = line.getOptionValue("C");
            Configuration config = new Configuration(pathToConfigFile);
            pathToImageMagick = config.getPathToImageMagick();
            pathToTesseract = config.getPathToTesseract();
            pathToTesseractConfigFile = config.getPathToTesseractConfigFile();
        }
        else{
            pathToConfigFile = null;
            pathToImageMagick = "/usr/bin/convert";
            pathToTesseract = "/usr/bin/tesseract";
            pathToTesseractConfigFile = "/usr/bin/config.txt";
        }
    }

    //~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-

    public boolean getHelp(){
        return help;
    }

    public ArrayList<String> getPubmedIDs(){
        return pubmedIDs;
    }

    public String getWorkspace(){
        return workspace;
    }
    public boolean getContainsPDFFiles(){
        return PDFFiles;
    }
    public String getQuery(){
        return query;
    }
    public String getPathToConfigFile(){
        return pathToConfigFile;
    }
    public String getPathToImageMagick(){
        return pathToImageMagick;
    }
    public String getPathToTesseract(){
        return pathToTesseract;
    }
    public String getPathToTesseractConfigFile(){
        return pathToTesseractConfigFile;
    }
}
