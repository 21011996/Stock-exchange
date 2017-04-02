package logic;

import files.File;
import messages.*;
import model.PurchaseRequest;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by heat_wave on 30.03.17.
 */
public class SellerLogic {
    private HashMap<String, File> files = new HashMap<>();
    private HashMap<String, ArrayList<PurchaseRequest>> purchaseRequests = new HashMap<>();
    private HashMap<String, PurchaseRequest> acceptedRequests = new HashMap<>();

    private Scanner in = new Scanner(System.in);
    private Node parent;

    public SellerLogic(Node parent) {
        this.parent = parent;
    }

    public SellerLogic(Node parent, Collection<File> collection) {
        this(parent);
        addFiles(collection);
    }

    public void setIn(Scanner in) {
        this.in = in;
    }

    public void addFiles(Collection<File> collection) {
        for (File file : collection) {
            addFile(file);
        }
    }

    public void addFile(File file) {
        if (files.containsKey(file.getName())) {
            if (!files.get(file.getName()).equals(file)) {
                Logger.getGlobal().log(Level.SEVERE, "Filename uniqueness violated");
            } else {
                Logger.getGlobal().log(Level.WARNING, "Repetitive addition of the same file detected");
            }
        } else {
            files.put(file.getName(), file);
        }
    }

    public File getFile(String filename) {
        return files.get(filename);
    }

    public HashMap<String, File> getFiles() {
        return files;
    }

    /**
     * Primary method of this class. Handles the bidding logic according to the type of incoming message.
     * @param message The network message to handle
     */
    public void onMessageReceived(Message message) {
        Logger.getGlobal().log(Level.FINER, message.toString());

        if (message instanceof RequestBuyMessage) {
            String filename = ((RequestBuyMessage) message).getRequestFile();
            int price = ((RequestBuyMessage) message).getRequestPrice();
            if (!files.containsKey(filename)) {
                Logger.getGlobal().log(Level.WARNING, "Request received for nonexistent file: " + filename);
            } else {
                purchaseRequests.putIfAbsent(filename, new ArrayList<>());
                purchaseRequests.get(filename).add(new PurchaseRequest(message.getName(), price));
                printBidStatus(filename, true);
            }
        } else if (message instanceof HaveMoneyMessage) {
            String requestedFile = ((HaveMoneyMessage) message).getFile().getName();
            String requestSender = message.getName();
            if (acceptedRequests.containsKey(requestedFile) &&
                    acceptedRequests.get(requestedFile).getNode().equals(message.getName())) {
                File toTransfer = files.get(requestedFile);
                toTransfer.setPrice(acceptedRequests.get(requestedFile).getOffer());
                parent.sendMessage(new TransferFileMessage(parent.getName(), toTransfer));
                File details = files.get(requestedFile);
                details.setPrice(acceptedRequests.get(requestedFile).getOffer());
                for (PurchaseRequest request : purchaseRequests.get(requestedFile)) {
                    parent.sendMessage(new NotifyBuyMessage(requestSender, details));
                }
                files.remove(requestedFile);
                purchaseRequests.remove(requestedFile);
                acceptedRequests.remove(requestedFile);
            } else {
                Logger.getGlobal().log(Level.SEVERE, "Attempting to force-buy the file. The message could have been fabricated. Malicious node: " + message.getName());
            }
        } else if (message instanceof BrokeMessage) {
            String fileName = ((BrokeMessage) message).getFile().getName();
            if (acceptedRequests.containsKey(fileName) &&
                    acceptedRequests.get(fileName).getNode().equals(message.getName())) {
                acceptedRequests.remove(fileName);
                System.out.println(String.format("Node %s has failed to pay for the file and has been removed from contention.", message.getName()));
                ArrayList<PurchaseRequest> requests = purchaseRequests.get(fileName);
                requests.removeIf(purchaseRequest -> purchaseRequest.getNode().equals(message.getName()));
                //AFAIR this should remove the request from the mapped arraylist, too
                printBidStatus(fileName, true);
            } else {
                Logger.getGlobal().log(Level.SEVERE, "Insufficient funds message without previous accepted bid. The message could have been fabricated. Malicious node: " + message.getName());
            }
        }
    }

    /**
     * Prints a single file's bids to the standard output. When in interactive mode, allows to choose a bid to accept.
     * @param filename The file to show status for
     * @param interactive Whether to interact with the user
     */
    private void printBidStatus(String filename, boolean interactive) {
        ArrayList<PurchaseRequest> requests = purchaseRequests.get(filename);
        requests.sort(Comparator.comparingInt(PurchaseRequest::getOffer).reversed());
        System.out.println(String.format("Bids for file %s(base price is %d) now: %d", filename, files.get(filename).getPrice(), requests.size()));
        for (int i = 0; i < requests.size(); i++) {
            System.out.println(String.format("#%d bid: %7d from node %s", i + 1,
                    requests.get(i).getOffer(), requests.get(i).getNode()));
        }
        if (interactive && requests.size() > 0) {
            System.out.println("Enter the number of a node to accept offer (\"0\" to reject all offers): ");
            String input = in.nextLine();
            try {
                int choice = Integer.parseInt(input);
                if (choice > 0 && choice <= requests.size()) {
                    acceptOffer(filename, requests.get(choice - 1));
                }
            } catch (NumberFormatException e) {
                System.out.println("Wrong input format; bids are ignored.");
            }
        }
    }

    /**
     * Utility method. Prints current status for all files
     */
    private void printAllBids() {
        for (String filename : files.keySet()) {
            printBidStatus(filename, false);
        }
    }

    private void acceptOffer(String filename, PurchaseRequest offer) {
        parent.sendMessage(new AcceptBuyMessage(parent.getName(), filename));
        acceptedRequests.put(filename, offer);
    }

}
