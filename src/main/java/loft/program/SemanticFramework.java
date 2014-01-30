package loft.program;


/**
 * Created for project: TableExtraction
 * In package: loft.program
 * Created with IntelliJ IDEA.
 * User: Sander van Boom
 * Date: 22-1-14
 * Time: 14:49
 */      /*
public class SemanticFramework {



    public static ArrayList<String> findXMLs(String workspace){
        ArrayList<String> PDFFiles = new ArrayList<String>();
        File dir = new File(workspace);
        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".xml");
            }
        });
        for(File file : files){
            PDFFiles.add(file.getName().substring(0, file.getName().length()-4));
        }
        return PDFFiles;
    }
    /**
     * This method returns the results of the extraction of the table and puts them in a XML format.
     * @param file This is the File which was used for the extraction of the Table
     * @param tableID This is the ID of the table that was extracted
     * @param semanticXML The results from the semantic framework in XML
     * @return This method returns a String containing the results of the Table extraction in valid XML.
     *
private String getXMLContent(File file, int tableID, String semanticXML){
        String fileContent = "<TEAFile>\n"+ getProvenance(file , tableID);
fileContent = fileContent + "    <results>\n";
fileContent = fileContent + "        <title>" + CommonMethods.changeIllegalXMLCharacters(name) + "</title>\n" ;
fileContent = fileContent + "        <title>" + CommonMethods.changeIllegalXMLCharacters(titleAndHeaders.toString()) + "</title>\n" ;
fileContent = fileContent + "        <columns>\n";
for(Column2 column : dataInColumns){
        fileContent = fileContent + "            <column>"+ CommonMethods.changeIllegalXMLCharacters(column.toString())+"</column>\n";
}
        fileContent = fileContent + "        </columns>\n";
if(rowSpanners.size() > 0){
        fileContent = fileContent +"        <rowSpanners>\n";
for(Line line : rowSpanners){
        fileContent = fileContent +"            <rowSpanner>" +CommonMethods.changeIllegalXMLCharacters(line.toString()) + "</rowSpanner>\n";
}
        fileContent = fileContent + "        </rowSpanners>\n";
}
        fileContent = fileContent + "    </results>\n";
fileContent = fileContent + semanticXML;
fileContent = fileContent + validation.toXML();
fileContent = fileContent + "</TEAFile>";
return fileContent;
}


/**
 * This method creates the provenance that is being used for writing the output.
 * @param file The file which was used to create this table.
 * @param tableID The unique ID of the table.
 * @return A string containing the provenance in XML format.
 *
private String getProvenance(File file, int tableID){
    return "    <provenance>\n"+
    "        <fromFile>" + file.getName()+"</fromFile>\n" +
    "        <fromPath>" + file.getAbsolutePath() +"</fromPath>\n"+
    "        <user>Sander</user>\n"+
    "        <detectionID>" + tableID +"</detectionID>\n"+
    "        <usedHorizontalThresholdModifier>"+horizontalThresholdModifier+"</usedHorizontalThresholdModifier>\n"+
    "        <usedVerticalThresholdModifier>"+verticalThresholdModifier+"</usedVerticalThresholdModifier>\n"+
    "    </provenance>\n";
}

/**
 * This method writes the results to the results directory in the workspace. Output is in XML.
 * @param filecontent The results as being collected during the reconstruction of this table.
 * @param location The path to the the XML file (output).
 * @param file The file which was used to reconstruct this table. (used for provenance and file-naming purpose)
 * @param tableID The ID of the table to make the path unique.
 * @throws java.io.IOException
 *
private void write(String filecontent, String location, File file, int tableID) throws IOException {
    LOGGER.info("Writing results to file: " + location + "/" + file.getName().substring(0, file.getName().length() - 5) + "-" + tableID + ".xml");
FileWriter fileWriter;
String writeLocation = location + "/results/" + file.getName().substring(0, file.getName().length() - 5) + "-" + tableID+ ".xml";
File newTextFile = new File(writeLocation);
fileWriter = new FileWriter(newTextFile);
fileWriter.write(filecontent);
fileWriter.close();
}
              /*
**
 * This method returns the semantic parts that have been calculated in valid XML format. This can be used in the output
 * of the program.
 * @return A string containing the content of the class in valid XML.
 *
public String getXML(){
    String content = "";
    content = content + "    <tableSemantics>\n";
    content = content + "        <title>"+ CommonMethods.changeIllegalXMLCharacters(title.toString())+"</title>\n";
    content = content + "        <titleConfidence>" +titleConfidence+"</titleConfidence>\n";
    if(!rowSpanners.isEmpty()){
        content = content + "        <subHeaders>" + CommonMethods.changeIllegalXMLCharacters(rowSpanners.toString()) + "</subHeaders>\n";
        content = content + "        <subHeadersConfidenceAlignment>" + identifiersConfidenceAlignment +"</subHeadersConfidenceAlignment>\n";
        content = content + "        <subHeadersConfidenceColumnsSpanned>" + identifiersConfidenceColumnsSpanned + "</subHeadersConfidenceColumnsSpanned>\n";
        content = content + "        <subHeadersConfidenceLineDistance>" + identifiersConfidenceLineDistance + "</subHeadersConfidenceLineDistance>\n";
    }
    if(!(validatedRowSpanners.isEmpty())){
        content = content + "        <rowSpanners>" + CommonMethods.changeIllegalXMLCharacters(validatedRowSpanners.toString())+"</rowSpanners>\n";
        content = content + "        <rowSpannersConfidenceAlignment>" + CommonMethods.makeListPositive(rowSpannersConfidenceAlignment) + "</rowSpannersConfidenceAlignment>\n";
        content = content + "        <rowSpannersConfidenceColumnsSpanned>" + rowSpannersConfidenceColumnsSpanned + "</rowSpannersConfidenceColumnsSpanned>\n";
        content = content + "        <rowSpannersConfidenceLineDistance>" + CommonMethods.addNumberToList(rowSpannersConfidenceLineDistance, 1.5) + "</rowSpannersConfidenceLineDistance>\n";
    }
    content = content + "        <headers>" + CommonMethods.changeIllegalXMLCharacters(headers.toString()) + "</headers>\n";
    content = content + "        <headerConfidence>" + headerConfidence + "</headerConfidence>\n";
    content = content + "    </tableSemantics>\n";
    return content;
}
} */
