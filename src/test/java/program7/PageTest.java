package program7;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class PageTest {
    private Page page;
    @Before
    public void PageTest() throws IOException {
        File file = new File("C:\\Users\\Sander van Boom\\IdeaProjects\\TableExtraction\\src\\test\\resources\\program7\\33-3.html.html");
        this.page = new Page(file, "C:\\Users\\Sander van Boom\\Documents\\School\\tables\\TEA0.7Test\\testFiles", false);
    }
    /**
     * This test is made of the original createTables method in the Page class to check if the table class is executed properly.
     */
    @Test
    public void createTablesTest1() throws IOException {
        System.out.println(page.createTables(2, 1.2));

        //assertEquals();
    }
}

