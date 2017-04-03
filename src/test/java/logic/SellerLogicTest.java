package logic;

import files.File;
import messages.BrokeMessage;
import messages.HaveMoneyMessage;
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
        sellerLogic.onMessageReceived(new RequestBuyMessage("wow", 90, "test"));
        assertEquals(sellerLogic.getPurReq().get("test").size(), 1);
        assertEquals(sellerLogic.getPurReq().get("test").get(0).getNode(),"wow");
        assertEquals(sellerLogic.getPurReq().get("test").get(0).getOffer(),90);
        File test = new File("test", 90);
        sellerLogic.acceptOffer("test",sellerLogic.getPurReq().get("test").get(0));
        sellerLogic.onMessageReceived(new HaveMoneyMessage("wow", test));
        assertEquals(sellerLogic.getAccReq().size(),0);
        assertEquals(sellerLogic.getPurReq().size(),0);
        assertEquals(sellerLogic.getF().size(),0);
        File test1 = new File("test1", 50);
        sellerLogic.addFile(test1);
        sellerLogic.onMessageReceived(new RequestBuyMessage("kek", 40, "test1"));
        sellerLogic.acceptOffer("test1",sellerLogic.getPurReq().get("test1").get(0));
        sellerLogic.onMessageReceived(new BrokeMessage("kek", test1));
        assertEquals(sellerLogic.getAccReq().size(),0);
    }

}