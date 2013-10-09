package templates;

/**
 * Created for project: TableExtraction
 * In package: program
 * Created with IntelliJ IDEA.
 * User: Sander van Boom
 * Date: 9-10-13
 * Time: 15:04
 */

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class TestTemplate {

    @Before
    public void initialiser(){
    }

    @Test
    public void Test(){
        String results = "test";
        assertEquals("test", results);
    }

}
