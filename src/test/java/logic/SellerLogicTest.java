package logic;

import files.File;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Created by heat_wave on 31.03.17.
 */
public class SellerLogicTest {
    private SellerLogic sellerLogic;

    @Before
    public void prepareLogic() {
        sellerLogic = new SellerLogic(Node.STUB);
    }

    @Test
    public void addFiles() throws Exception {
        ArrayList<File> testFiles = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            testFiles.add(new File("testfile" + i, i * 100));
        }
        sellerLogic.addFiles(testFiles);
        for (File file : testFiles) {
            assertEquals(file, sellerLogic.getFile(file.getName()));
        }
    }

    @Test
    public void addFile() throws Exception {
        File testFile = new File("testfile", 100);
        sellerLogic.addFile(testFile);
        assertEquals(testFile, sellerLogic.getFile(testFile.getName()));
    }



}