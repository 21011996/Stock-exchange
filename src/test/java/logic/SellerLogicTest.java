package logic;

import files.File;
import messages.RequestBuyMessage;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * Created by heat_wave on 31.03.17.
 */
public class SellerLogicTest {
    private SellerLogic sellerLogic;

    @Before
    public void prepareLogic() {
        sellerLogic = new SellerLogic(new Node("TestNode", new ParticipantState(100, new HashMap<String, File>() {{
            put("test", new File("test", 100));
        }}), true));
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

    @Test
    public void sellFile() {
        sellerLogic.addFile(new File("file1", 100));
        sellerLogic.onMessageReceived(new RequestBuyMessage("wow", 90, "file1"));
        assertEquals(sellerLogic.purchaseRequests.get("file1").size(), 1);
    }

}