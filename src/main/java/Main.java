import files.File;
import logic.SellerLogic;
import messages.HaveMoneyMessage;
import messages.RequestBuyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static logic.Node.STUB;

/**
 * @author @ilya2
 *         created on 29.03.2017
 */
public class Main {
    private Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        new Main().run();
    }

    public void run() {
        SellerLogic sellerLogic;
        //Node node = new Node("TestNode", new ParticipantState(100, Collections.singletonList(new File("test", 100)), new HashSet<>(), new HashMap<>()));
        sellerLogic = new SellerLogic(STUB);
        sellerLogic.addFile(new File("test", 100));
        sellerLogic.onMessageReceived(new RequestBuyMessage("Test", 101, "test"));
        sellerLogic.onMessageReceived(new RequestBuyMessage("Test2", 102, "test"));
        sellerLogic.onMessageReceived(new RequestBuyMessage("Test3", 103, "test"));
        sellerLogic.onMessageReceived(new HaveMoneyMessage("Test3", new File("test", 103)));
        System.out.println(sellerLogic.getFiles());
    }
}
