package files;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by disoni on 02.04.17.
 */
public class FileTest {
    private File testFile;
    private String testString = "examplefile=1000";

    @Test
    public void parseFile() throws Exception {
        testFile = File.parseFile(testString) ;
        assertEquals(testFile.getName(),"examplefile");
        assertEquals(testFile.getPrice(),1000);
    }
}
