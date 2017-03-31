package logic;

import messages.Message;

/**
 * Created by heat_wave on 30.03.17.
 */
public class Node {
    public static Node STUB = new Node();

    private NetworkLogic networkLogic;
    private SellerLogic sellerLogic;

    void sendMessage(String node, Message message) {
        //networkLogic.send(node, message);
        System.out.println("Test: " + node + "->" + message);
    }

    /**
     *
     * @return A unique name of this node
     */
    public String getName() {
        return "SellerTestNode";
        //return this.toString();
    }
}
