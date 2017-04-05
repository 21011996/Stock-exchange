package logic;

import messages.HelloMessage;
import messages.Message;
import model.Envelope;
import network.FixedAddressesNetworkLogicImpl;
import network.NetworkLogicImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Node {
    public final String name;
    private final NetworkLogic networkLogic;
    private final SellerLogic sellerLogic;
    private final BuyerLogic buyerLogic;
    private final BlockingQueue<Envelope> messagesToSend = new LinkedBlockingDeque<>();
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
     
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        networkLogic = NetworkLogicImpl.Companion.buildFromConfig(this.name, this);
        networkLogic.addMessageHandler(messagesToHandle::add);


        System.out.println(currentState.getDocuments().toString());
        System.out.println("Usage:\n" +
                "- To buy a file: buy %filename% %bid%\n" +
                "- To sell the requested file: sell %filename% %nodename%\n" +
                "- To reject the bid: reject %filename% %nodename% %reason%\n" +
                "- To list all available files to buy: listfiles\n" +
                "- To see your balance and files: status\n" +
                "- To see all open bids for your files: infoall\n" +
                "- To see all open bids for your particular file: info %filename%\n" +
                "- To terminate: exit");
    }

    public Node(String name, ParticipantState participantState, int i) {
        this.name = name;
        this.currentState = participantState;
        sellerLogic = new SellerLogic(this);
        buyerLogic = new BuyerLogic(this);
        networkLogic = FixedAddressesNetworkLogicImpl.Companion.buildFromConfig(this.name, this);
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

    public HelloMessage generateHelloMessage() {
        return new HelloMessage(getName(), getCurrentState().getBalance(), new ArrayList<>(getCurrentState().getDocuments().values()));
    }

    public void shutdown() {
        logger.info("Shutting down node {}", name);
        sendingThread.interrupt();
        handlingThread.interrupt();
        consoleThread.interrupt();
    }

    void sendMessage(String to, Message message) {
        logger.info("Sending {} to {}", message.toString(), to);
        messagesToSend.add(new Envelope(to, message));
    }

    // Run in a separate thread
    private void sendMessagesLoop() {
        Thread.currentThread().setName("SendMessagesLoop");
        while (!Thread.interrupted()) {
            try {
                Envelope m = messagesToSend.take();
                networkLogic.send(m.getDestination(), m.getMessage());
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