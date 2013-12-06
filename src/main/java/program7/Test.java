package program7;

import java.io.File;
import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        File file = new File("./src/test/resources/program7/3-7.html.html");
        Page page = new Page(file, "C:\\Users\\Sander van Boom\\Documents\\School\\tables\\TEA0.7Test\\resources");
        page.createTables(2,1.2);
    }
}
