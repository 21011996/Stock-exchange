package logic;

import files.File;
import messages.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Node {
    public static Node STUB = new Node("stub", new ParticipantState(
            1000,
            Arrays.asList(File.parseFile("a=123"), File.parseFile("b=456")),
            new HashSet<>(),
            new HashMap<>()), true);
    public final String name;
    private final NetworkLogic networkLogic = new NetworkLogic();
    private final SellerLogic sellerLogic;
    private final BuyerLogic buyerLogic = new BuyerLogic(this);
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
        this.currentState = participantState;
        sellerLogic = new SellerLogic(this, currentState.getDocuments());
        networkLogic.addMessageHandler(messagesToHandle::add);

        logger.info("Starting consoleThread");
        asyncConsoleReader = new AsyncConsoleReader(this, sellerLogic);
        consoleThread = new Thread(asyncConsoleReader);
        consoleThread.start();
        logger.info("Starting sendingThread");
        sendingThread = new Thread(this::sendMessagesLoop);
        sendingThread.start();
        logger.info("Starting handlingThread");
        handlingThread = new Thread(this::handleMessagesLoop);
        handlingThread.start();
    }

    public Node(String name, ParticipantState participantState, boolean stub) {
        this.name = name;
        this.currentState = participantState;
        sellerLogic = new SellerLogic(this, currentState.getDocuments());
        networkLogic.addMessageHandler(messagesToHandle::add);
        asyncConsoleReader = new AsyncConsoleReader(this, sellerLogic);

        if (stub) {
            sendingThread = new Thread(this::sendMessagesLoop);
            handlingThread = new Thread(this::handleMessagesLoop);
            consoleThread = new Thread(asyncConsoleReader);
        } else {
            consoleThread = new Thread(asyncConsoleReader);
            consoleThread.start();
            sendingThread = new Thread(this::sendMessagesLoop);
            sendingThread.run();
            handlingThread = new Thread(this::handleMessagesLoop);
            handlingThread.run();
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
        logger.info("messagesToHandle {}", messagesToHandle.toString());
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