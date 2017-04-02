package logic;

import files.File;
import messages.AcceptBuyMessage;
import messages.TransferFileMessage;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import static org.junit.Assert.assertEquals;

/**
 * Created by shambala on 03.04.17.
 */
public class BuyerLogicTest {
    private BuyerLogic buyerLogic;

    @Before
    public void prepare() {
        buyerLogic = new BuyerLogic(new Node("Test", new ParticipantState(1000, new HashMap<>()), true));
    }

    @Test
    public void requestFile() {
        HashMap<String, File> map = new HashMap<>();
        System.out.println(buyerLogic.files.toString());
        map.put("file1", new File("file1", 10));

        Node seller = new Node("Test1", new ParticipantState(100, map));
        buyerLogic.wantToBuy(seller, "file1", 100);
        buyerLogic.onMessageReceived(new AcceptBuyMessage("kek", "file1"));
        buyerLogic.onMessageReceived(new TransferFileMessage("lol", map.get("file1")));
        assertEquals(map, buyerLogic.files);
    }
}
