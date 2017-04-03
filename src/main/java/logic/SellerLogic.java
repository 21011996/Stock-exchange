package logic;

import files.File;
import messages.*;
import model.PurchaseRequest;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by heat_wave on 30.03.17.
 */
public class SellerLogic {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(SellerLogic.class);
    HashMap<String, File> files;
    HashMap<String, ArrayList<PurchaseRequest>> purchaseRequests = new HashMap<>();
    HashMap<String, PurchaseRequest> acceptedRequests = new HashMap<>();

    private Scanner in = new Scanner(System.in);
    private Node parent;

    public SellerLogic(Node parent) {
        this.parent = parent;
        files = parent.getCurrentState().getDocuments();
    }

    public SellerLogic(Node parent, Collection<File> collection) {
        this(parent);
        parent.getCurrentState().addDocuments(collection);
    }

    public void setIn(Scanner in) {
        this.in = in;
    }

    public void addFiles(Collection<File> files) {
        parent.getCurrentState().addDocuments(files);
    }

    public void addFile(File file) {
        parent.getCurrentState().addDocument(file);
    }

    public File getFile(String fileName) {
        return parent.getCurrentState().getDocument(fileName);
    }


    /**
     * Primary method of this class. Handles the bidding logic according to the type of incoming message.
     *
     * @param message The network message to handle
     */
    public void onMessageReceived(Message message) {
        logger.info("Received {}", message.toString());

        if (message instanceof RequestBuyMessage) {
            String filename = ((RequestBuyMessage) message).getRequestFile();
            logger.debug("Adding request to bids for file {}", filename);
            int price = ((RequestBuyMessage) message).getRequestPrice();
            if (!files.containsKey(filename)) {
                logger.warn("Request received for nonexistent file: {}", filename);
            } else {
                purchaseRequests.putIfAbsent(filename, new ArrayList<>());
                purchaseRequests.get(filename).add(new PurchaseRequest(message.getName(), price));
            }
        } else if (message instanceof HaveMoneyMessage) {
            String requestedFile = ((HaveMoneyMessage) message).getFile().getName();
            String requestSender = message.getName();
            logger.debug("Received money for {}", requestedFile);
            if (acceptedRequests.containsKey(requestedFile) &&
                    acceptedRequests.get(requestedFile).getNode().equals(message.getName())) {
                File toTransfer = files.get(requestedFile);
                toTransfer.setPrice(acceptedRequests.get(requestedFile).getOffer());
                parent.sendMessage(requestSender, new TransferFileMessage(parent.getName(), toTransfer));
                parent.getCurrentState().addRemoteDocument(toTransfer, requestSender);
                File details = files.get(requestedFile);
                details.setPrice(acceptedRequests.get(requestedFile).getOffer());
                for (PurchaseRequest request : purchaseRequests.get(requestedFile)) {
                    parent.sendMessage(requestSender, new NotifyBuyMessage(requestSender, details)); //TODO: Looks like bug, request is not used
                }
                files.remove(requestedFile);
                purchaseRequests.remove(requestedFile);
                acceptedRequests.remove(requestedFile);
            } else {
                logger.debug("Attempting to force-buy the file. The message could have been fabricated. Malicious node: {}", message.getName());
            }
        } else if (message instanceof BrokeMessage) {
            String fileName = ((BrokeMessage) message).getFile().getName();
            logger.debug("Node couldn't send money for {}", fileName);
            if (acceptedRequests.containsKey(fileName) &&
                    acceptedRequests.get(fileName).getNode().equals(message.getName())) {
                acceptedRequests.remove(fileName);
                System.out.println(String.format("Node %s has failed to pay for the file and has been removed from contention.", message.getName()));
                ArrayList<PurchaseRequest> requests = purchaseRequests.get(fileName);
                requests.removeIf(purchaseRequest -> purchaseRequest.getNode().equals(message.getName()));
                //AFAIR this should remove the request from the mapped arraylist, too
            } else {
                logger.debug("Insufficient funds message without previous accepted bid. The message could have been fabricated. Malicious node: {}", message.getName());
            }
        }
    }

    /**
     * Prints a single file's bids to the standard output. When in interactive mode, allows to choose a bid to accept.
     *
     * @param fileName The file to show status for
     */
    public void printBidStatus(String fileName) {
        if (!purchaseRequests.containsKey(fileName)) {
            System.out.println("No such file");
            return;
        }
        ArrayList<PurchaseRequest> requests = purchaseRequests.get(fileName);
        requests.sort(Comparator.comparingInt(PurchaseRequest::getOffer).reversed());
        System.out.println(String.format("Bids for file %s(base price is %d) now: %d", fileName, files.get(fileName).getPrice(), requests.size()));
        for (int i = 0; i < requests.size(); i++) {
            System.out.println(String.format("#%d bid: %7d from node %s", i + 1,
                    requests.get(i).getOffer(), requests.get(i).getNode()));
        }

    }

    public void sellFile(String fileName, String node) {
        if (!purchaseRequests.containsKey(fileName)) {
            System.out.println("No such file");
            return;
        }
        ArrayList<PurchaseRequest> requests = purchaseRequests.get(fileName);
        boolean sold = false;
        for (PurchaseRequest pr : requests) {
            if (node.equals(pr.getNode())) {
                acceptOffer(fileName, pr);
                sold = true;
                break;
            }
        }
        if (!sold) {
            System.out.println(String.format("%s doesn't want to buy %s", node, fileName));
        }
    }

    /**
     * Utility method. Prints current status for all files
     */
    public void printAllBids() {
        for (String filename : files.keySet()) {
            printBidStatus(filename);
        }
    }

    public void acceptOffer(String filename, PurchaseRequest offer) {
        parent.sendMessage(offer.getNode(), new AcceptBuyMessage(parent.getName(), filename));
        acceptedRequests.put(filename, offer);
    }

}
