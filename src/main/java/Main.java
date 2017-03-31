import files.File;
import logic.Node;
import logic.SellerLogic;
import messages.BrokeMessage;
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
        sellerLogic.onMessageReceived(new BrokeMessage("Test", new File("test", 101)));
        System.out.println(sellerLogic.getFiles());
    }
}
