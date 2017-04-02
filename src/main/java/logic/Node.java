package logic;

import messages.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Node {
    public final String name;
    private final NetworkLogic networkLogic = new NetworkLogic();
    private final SellerLogic sellerLogic;
    private final BuyerLogic buyerLogic;
    private final BlockingQueue<Message> messagesToSend = new LinkedBlockingDeque<>();
    private final BlockingQueue<Message> messagesToHandle = new LinkedBlockingDeque<>();
    private final Thread sendingThread;
    private final Thread handlingThread;
    private final Thread consoleThread;
    private final AsyncConsoleReader asyncConsoleReader;

    private Logger logger = LoggerFactory.getLogger(Node.class);
    private ParticipantState currentState;

    public Node(String name, ParticipantState participantState) {
        this.name = name;
        Thread.currentThread().setName(this.getName());
        this.currentState = participantState;
        sellerLogic = new SellerLogic(this);
        buyerLogic = new BuyerLogic(this);
        networkLogic.addMessageHandler(messagesToHandle::add);

        logger.info("Starting consoleThread");
        asyncConsoleReader = new AsyncConsoleReader(this, sellerLogic, buyerLogic);
        consoleThread = new Thread(asyncConsoleReader);
        consoleThread.start();
      
        logger.info("Starting sendingThread");
        sendingThread = new Thread(this::sendMessagesLoop);
        sendingThread.start();
        logger.info("Starting handlingThread");
        handlingThread = new Thread(this::handleMessagesLoop);
        handlingThread.start();
    }

    public Node(String name, ParticipantState participantState, int i) {
        this.name = name;
        this.currentState = participantState;
        sellerLogic = new SellerLogic(this);
        buyerLogic = new BuyerLogic(this);
        networkLogic.addMessageHandler(messagesToHandle::add);

        logger.info("Starting consoleThread");
        asyncConsoleReader = new AsyncConsoleReader(this, sellerLogic, buyerLogic);
        consoleThread = new Thread(asyncConsoleReader);
        consoleThread.start();
      
        sendingThread = new Thread(this::sendMessagesLoop);
        handlingThread = new Thread(this::handleMessagesLoop);
    }

    public Node(String name, ParticipantState participantState, boolean stub) {
        this(name, participantState, 0);
        if (!stub) {
            logger.info("Starting sending thread");
            sendingThread.start();
            logger.info("Starting handling thread");
            handlingThread.start();
        }
    }

    public ParticipantState getCurrentState() {
        return currentState;
    }

    public String getName() {
        return name;
    }

    public void shutdown() {
        logger.info("Shutting down node {}", name);
        sendingThread.interrupt();
        handlingThread.interrupt();
        consoleThread.interrupt();
    }

    void sendMessage(Message message) {
        logger.info("Sending {}", message.toString());
        messagesToSend.add(message);
    }

    // Run in a separate thread
    private void sendMessagesLoop() {
        Thread.currentThread().setName("SendMessagesLoop");
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
        logger.info("Received {} -> {}", node, message);
        sellerLogic.onMessageReceived(message);
        buyerLogic.onMessageReceived(message);
    }

    public void addMessage(Message message) throws InterruptedException {
        messagesToHandle.add(message);
    }
    // Run in a separate thread
    private void handleMessagesLoop() {
        Thread.currentThread().setName("HandleMessagesLoop");
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