package program7;


import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.logging.Logger;

//This class processes the arguments that the user has put in the commandline calling this process.
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
    private double verticalThresholdModifier;
    private double horizontalThresholdModifier;


    //TODO: Create a Debug option that outputs various system.outs to debug files.
    /**
     * The constructor of this class creates a commandline with the correct options (as was given by the user) and sets
     * the local variables.
     * @param args The arguments as given by the user.
     * @throws org.apache.commons.cli.ParseException
     * @throws java.io.IOException
     */
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
    //The setters of this class:

    /**
     * This method sets the help boolean to check if someone used -H in their command.
     * @param line the Commandline as made by the constructor.
     * @return a boolean that is only true when the commandline contains the help option
     */
    private boolean setHelp(CommandLine line){
        return(line.hasOption("H"));
    }

    /**
     * This method set the pubmed IDS from the pubmedFile option.
     * @param line the commandline as made by the constructor
     * @return An ArrayList containing the pubmedID's that were extracted from the file.
     * @throws java.io.IOException
     */
    private ArrayList<String> setPubmedIDs(CommandLine line) throws IOException {
        String pubmedFile;
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

    /**
     * This methods receives the workspace parameter from the arguments.
     * @param line the commandline as made by the constructor
     * @return A string containing the full path to the workspace.
     */
    private String setWorkspace(CommandLine line){
        String workspace;
        if(line.hasOption("W")){
            workspace = line.getOptionValue("W");
            LOGGER.info("workspace set to: " + workspace);
            return workspace;
        }
        else{
            return null;
        }
    }

    /**
     * This method checks when the PDF option is enabled by the user.
     * @param line the commandline as made by the constructor
     * @return a boolean that is only true when the user used -PDF in his arguments.
     */
    private boolean setPDFFiles(CommandLine line){
        return line.hasOption("PDF");
    }

    /**
     * This method receives the Query if it was added as input by the user.
     * @param line the commandline as made by the constructor
     * @return The query that was given after -QUE. Returns null when no query was given.
     */
    private String setQuery(CommandLine line) throws UnsupportedEncodingException {
        String query = null;
        if(line.hasOption("QUE")){
            query = line.getOptionValue("QUE");
            query = URLEncoder.encode(query, "UTF-8");
            LOGGER.info("Used query: " +query);
        }
        return query;
    }

    /**
     * This method checks if the user added a configuration file. If so it creates a Configuration object
     * and calls the getters of this class to set local variables that have to do with the configuration file.
     * @param line the commandline as made by the constructor
     * @throws java.io.IOException
     */
    private void setPathToConfigFileValues(CommandLine line) throws IOException {
        if(line.hasOption("C")){
            LOGGER.info("Configuration file detected.");
            pathToConfigFile = line.getOptionValue("C");
            Configuration config = new Configuration(pathToConfigFile);
            pathToImageMagick = config.getPathToImageMagick();
            pathToTesseract = config.getPathToTesseract();
            pathToTesseractConfigFile = config.getPathToTesseractConfigFile();
            verticalThresholdModifier = config.getVerticalThresholdModifier();
            horizontalThresholdModifier = config.getHorizontalThresholdModifier();
        }
        else{
            LOGGER.warning("No configuration file detected! Working with default installation locations!");
            pathToConfigFile = null;
            pathToImageMagick = "/usr/bin/convert";
            pathToTesseract = "/usr/bin/tesseract";
            pathToTesseractConfigFile = "/usr/bin/config.txt";
            verticalThresholdModifier = 1.0;
            horizontalThresholdModifier = 2.0;
        }
    }

    //~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-
    //The getters of this object:

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
    @SuppressWarnings("UnusedDeclaration")          //We want to offer these methods for future use.
    public String getQuery(){
        return query;
    }
    @SuppressWarnings("UnusedDeclaration")          //We want to offer these methods for future use.
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
    public double getVerticalThresholdModifier(){
        return verticalThresholdModifier;
    }
    public double getHorizontalThresholdModifier(){
        return horizontalThresholdModifier;
    }
}
