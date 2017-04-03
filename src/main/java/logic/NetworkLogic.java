package logic;

import messages.Message;

import java.util.function.Consumer;

/**
 * Created by heat_wave on 30.03.17.
 */

interface NetworkLogic {
    /**
     * A handle that sends a message to a node over the network.
     * @param node The addressee
     * @param message The message
     */
    void send(String node, Message message);
    //TODO: Parse the network information from the node

    void addMessageHandler(Consumer<? super Message> handler);
}
