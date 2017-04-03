package logic;

import files.File;
import messages.AcceptBuyMessage;
import messages.HelloMessage;
import messages.TransferFileMessage;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.assertEquals;

/**
 * Created by shambala on 03.04.17.
 */
public class BuyerLogicTest {
    private BuyerLogic buyerLogic;

    @Before
    public void prepare() {
        ParticipantState  state = new ParticipantState(1000, new HashMap<>());
        buyerLogic = new BuyerLogic(new Node("Test", state, true));
    }

    @Test
    public void requestFile() {
        HashMap<String, File> map = new HashMap<>();
        map.put("file1", new File("file1", 100));
        ArrayList<File> files = new ArrayList<>();
        files.add(new File("file1", 10));
        buyerLogic.onMessageReceived(new HelloMessage("wow", 100, files));
        assertEquals(buyerLogic.parent.getCurrentState().getRemoteDocuments().size(), 1);
        assertEquals(buyerLogic.parent.getCurrentState().getDocumentNode("file1"), "wow");
        buyerLogic.wantToBuy("file1", 100);
        buyerLogic.onMessageReceived(new AcceptBuyMessage("kek", "file1"));
        buyerLogic.onMessageReceived(new TransferFileMessage("lol", new File("file1", 100)));
        assertEquals(map.get("file1").getPrice(), buyerLogic.files.get("file1").getPrice());
        assertEquals(map.get("file1").getName(), buyerLogic.files.get("file1").getName());
        assertEquals(buyerLogic.files.size(),1);
        assertEquals(buyerLogic.parent.getCurrentState().getRemoteDocuments().size(), 0);
    }
}
