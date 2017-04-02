import files.File;
import logic.Node;
import logic.ParticipantState;
import messages.HaveMoneyMessage;
import messages.RequestBuyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * @author @ilya2
 *         created on 29.03.2017
 */
public class Main {
    private Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException {
        new Main().run();
    }

    public void run() throws InterruptedException {
        Node node = new Node("TestNode", new ParticipantState(100, new HashMap<String, File>() {{
            put("test", new File("test", 100));
        }}));
        node.addMessage(new RequestBuyMessage("Test", 101, "test"));
        node.addMessage(new RequestBuyMessage("Test2", 102, "test"));
        node.addMessage(new RequestBuyMessage("Test3", 103, "test"));
        node.addMessage(new HaveMoneyMessage("Test3", new File("test", 103)));
        System.out.println(node.getCurrentState().getDocuments());
        //node.shutdown();
    }
}
