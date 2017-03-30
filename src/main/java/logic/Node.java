package logic;

import messages.Message;

/**
 * Created by heat_wave on 30.03.17.
 */
public class Node {
    public static Node STUB = new Node();

    private NetworkLogic networkLogic;
    private SellerLogic sellerLogic;

    void sendMessage(Node node, Message message) {
        networkLogic.send(node, message);
    }

    /**
     *
     * @return A unique name of this node
     */
    public String getName() {
        return this.toString();
    }
}
