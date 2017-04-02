package logic;

import files.File;
import messages.Message;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Node {
    public static Node STUB = new Node("stub", new ParticipantState(
            1000,
            Arrays.asList(File.parseFile("a=123"), File.parseFile("b=456")),
            Collections.emptySet(),
            Collections.emptyMap()));

    public final String name;

    private final NetworkLogic networkLogic = new NetworkLogic();
    private ParticipantState currentState;
    private final SellerLogic sellerLogic = new SellerLogic(this, currentState.getDocuments());
    private final BuyerLogic buyerLogic = new BuyerLogic(this);

    private final BlockingQueue<Message> messagesToSend = new LinkedBlockingDeque<>();
    private final BlockingQueue<Message> messagesToHandle = new LinkedBlockingDeque<>();

    private final Thread sendingThread;
    private final Thread handlingThread;

    public Node(String name, ParticipantState participantState) {
        this.name = name;
        this.currentState = participantState;

        networkLogic.addMessageHandler(messagesToHandle::add);

        sendingThread = new Thread(this::sendMessagesLoop);
        sendingThread.run();
        handlingThread = new Thread(this::handleMessagesLoop);
        handlingThread.run();
    }

    public String getName() {
        return name;
    }

    public void shutdown() {
        sendingThread.interrupt();
        handlingThread.interrupt();
    }

    void sendMessage(Message message) {
        System.out.println("message to send" + message);
        messagesToSend.add(message);
    }

    // Run in a separate thread
    private void sendMessagesLoop() {
        while (!Thread.interrupted()) {
            try {
                Message m = messagesToSend.take();
                networkLogic.send(m.getName(), m);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private void handleMessage(String node, Message message) {
        System.out.println("message to send: " + node + "->" + message);
        sellerLogic.onMessageReceived(message);
        buyerLogic.onMessageReceived(message);
    }

    // Run in a separate thread
    private void handleMessagesLoop() {
        while (!Thread.interrupted()) {
            try {
                Message m = messagesToHandle.take();
                handleMessage(m.getName(), m);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}