package program6;

import java.io.File;
import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        File file = new File("./src/test/resources/program6/23-3.html.html");
        Page page = new Page(file, "\"C:\\Users\\Sander van Boom\\Documents\\School\\tables\\TEA0.6Test\\resources\"");
        page.createTables();
    }
}
