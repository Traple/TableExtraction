package program7;

import java.io.File;
import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        File file = new File("C:\\Users\\Sander van Boom\\Documents\\School\\tables\\TEA0.7RandomCorpus2\\resources\\42.full-3.html");
        Page page = new Page(file, "C:\\Users\\Sander van Boom\\Documents\\School\\tables\\TEA0.7RandomCorpus2\\resources", false);
        page.createTables(2,1.2);
    }
}
