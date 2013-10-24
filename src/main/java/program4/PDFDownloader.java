package program4;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class PDFDownloader {

    private String PDFURL;
    private String outputLocation;

    public PDFDownloader(String PDFURL, String outputLocation) throws IOException {
        this.PDFURL = PDFURL;
        this.outputLocation = outputLocation;

        URLConnection conn = new URL(PDFURL).openConnection();
        InputStream is = conn.getInputStream();

        OutputStream outstream = new FileOutputStream(new File(outputLocation));
        byte[] buffer = new byte[4096];
        int len;
        while ((len = is.read(buffer)) > 0) {
            outstream.write(buffer, 0, len);
        }
        outstream.close();
    }
}
