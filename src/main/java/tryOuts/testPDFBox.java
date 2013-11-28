package tryOuts;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class testPDFBox {
    public static void main (String[] args) throws IOException {
        String text = getContentFromPDF(getPDFStream());
        System.out.println(text);
    }
    public static InputStream getPDFStream() throws IOException{
        File con = new File("C:\\Users\\Sander van Boom\\Dropbox\\Tables and Figures\\Corpus 1.1\\CorpusParamMain\\37.pdf");
//        con.setRequestProperty("Cookie", cookie);
        return new BufferedInputStream(new FileInputStream(con));
    }

    private static String getContentFromPDF(InputStream stream) throws IOException
    {
        PDFTextStripper stripper = new PDFTextStripper();

        PDDocument doc;
        try
        {
            doc = PDDocument.load(stream);
        }
        catch(IOException e)
        {
            return null;
        }
        String text = stripper.getText(doc);
        doc.close();
        text = text.replaceAll("-\\n", "-");
        text = text.replaceAll("\\n", " ");
        text = text.replaceAll("[^A-Za-z\\s(){}_,.:;<>!=&\\-+\"'0-9|%]", " ");
        text = text.replaceAll("\\r", " ");
        return text;
    }
}
