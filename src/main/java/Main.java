import files.File;
import logic.Node;
import logic.SellerLogic;
import messages.HaveMoneyMessage;
import messages.RequestBuyMessage;

/**
 * @author @ilya2
 *         created on 29.03.2017
 */
public class Main {
    public static void main(String[] args) {
        SellerLogic sellerLogic;
        sellerLogic = new SellerLogic(Node.STUB);
        System.out.println("Testing onReceive");
        sellerLogic.addFile(new File("test", 100));
        sellerLogic.onMessageReceived(new RequestBuyMessage("Test", 101, "test"));
        sellerLogic.onMessageReceived(new RequestBuyMessage("Test2", 102, "test"));
        sellerLogic.onMessageReceived(new RequestBuyMessage("Test3", 103, "test"));
        sellerLogic.onMessageReceived(new HaveMoneyMessage("Test3", new File("test", 103)));
        System.out.println(sellerLogic.getFiles());
    }
}
