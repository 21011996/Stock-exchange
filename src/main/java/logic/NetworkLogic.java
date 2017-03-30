package logic;

import messages.Message;

/**
 * Created by heat_wave on 30.03.17.
 */

public class NetworkLogic {
    //private Map<String, NetworkInfo> addressBook = new Map<>();

    /**
     * A handle that sends a message to a node over the network.
     * @param node The addressee
     * @param message The message
     */
    public void send(String node, Message message) {
        //TODO: Parse the network information from the node
    }

    /**
     * The "real" message transfer mechanism. Uses a node's network address to send the data.
     */
    private void send() {};
}
