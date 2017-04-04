import files.File;
import logic.Node;
import logic.ParticipantState;
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
        //new Main().run();
        Node node = new Node(args[0], new ParticipantState(1000, new HashMap<String, File>() {{
            put("test" + args[0], new File("test" + args[0], 100));
        }}));
    }

    public void run() throws InterruptedException {
        byte[] temp = new byte[]{100, 102, 103, 104};
        System.out.println(new String(temp));
        /*Thread.currentThread().setName("Main");
        */
        Node node = new Node("node1", new ParticipantState(100, new HashMap<String, File>() {{
            put("test", new File("test", 100));
        }}));
        /*
        ThreadInfo[] threads = ManagementFactory.getThreadMXBean()
                .dumpAllThreads(true, true);
        for (final ThreadInfo info : threads)
            System.out.print(info);
        node.addMessage(new RequestBuyMessage("Test", 101, "test"));
        node.addMessage(new RequestBuyMessage("Test2", 102, "test"));
        node.addMessage(new RequestBuyMessage("Test3", 103, "test"));
        Thread.sleep(10000);
        node.addMessage(new HaveMoneyMessage("Test3", new File("test", 103)));
        Thread.sleep(1000);
        System.out.println(node.getCurrentState().getDocuments());
        node.shutdown();
        Thread.sleep(1000);
        threads = ManagementFactory.getThreadMXBean()
                .dumpAllThreads(true, true);
        for (final ThreadInfo info : threads)
            System.out.print(info);*/
    }
}
