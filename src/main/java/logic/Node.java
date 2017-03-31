package logic;

import files.File;
import messages.Message;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Node {
    public static Node STUB = new Node("stub");

    public final String name;

    private final NetworkLogic networkLogic = new NetworkLogic();
    private ParticipantState currentState = initState();
    private final SellerLogic sellerLogic = new SellerLogic(this, currentState.getDocuments());

    private final BlockingQueue<AddressedMessage> messagesToSend = new LinkedBlockingDeque<>();
    private final BlockingQueue<AddressedMessage> messagesToHandle = new LinkedBlockingDeque<>();

    private final Thread sendingThread;
    private final Thread handlingThread;

    public Node(String name) {
        this.name = name;

        networkLogic.addMessageHandler(messagesToHandle::add);

        sendingThread = new Thread(this::sendMessagesLoop);
        sendingThread.run();
        handlingThread = new Thread(this::handleMessagesLoop);
        handlingThread.run();
    }

    public String getName() {
        return name;
    }

    private ParticipantState initState() {
        //todo read from file or console or whatever
        return new ParticipantState(
                1000,
                Arrays.asList(File.parseFile("a=123"), File.parseFile("b=456")),
                Collections.emptySet(),
                Collections.emptyMap());
    }

    public void shutdown() {
        sendingThread.interrupt();
        handlingThread.interrupt();
    }

    void sendMessage(String node, Message message) {
        System.out.println("message to send: " + node + "->" + message);
        messagesToSend.add(new AddressedMessage(node, message));
    }

    // Run in a separate thread
    private void sendMessagesLoop() {
        while (!Thread.interrupted()) {
            try {
                AddressedMessage m = messagesToSend.take();
                networkLogic.send(m.node, m.message);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private void handleMessage(String node, Message message) {
        System.out.println("message to send: " + node + "->" + message);
        //todo forward the message to sellerLogic, update currentState etc.
    }

    // Run in a separate thread
    private void handleMessagesLoop() {
        while (!Thread.interrupted()) {
            try {
                AddressedMessage m = messagesToHandle.take();
                handleMessage(m.node, m.message);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}

class AddressedMessage {
    public final String node;
    public final Message message;

    AddressedMessage(String node, Message message) {
        this.node = node;
        this.message = message;
    }
}