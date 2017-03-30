package logic;

import files.File;
import messages.AcceptBuyMessage;
import messages.Message;
import messages.RequestBuyMessage;
import model.PurchaseRequest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by heat_wave on 30.03.17.
 */
public class SellerLogic {
    private HashMap<String, File> files = new HashMap<>();
    private HashMap<String, ArrayList<PurchaseRequest>> purchaseRequests = new HashMap<>();

    private Scanner in = new Scanner(System.in);
    private Node parent;

    public SellerLogic(Node parent) {
        this.parent = parent;
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
                purchaseRequests.get(filename).add(new PurchaseRequest(Node.STUB, price));
                printBidStatus(filename, true);
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
        requests.sort(Comparator.comparingInt(PurchaseRequest::getOffer));
        System.out.println(String.format("Bids for file %s now: %d", filename, requests.size()));
        for (int i = 0; i < requests.size(); i++) {
            System.out.println(String.format("#%d bid: %7d from node %s", i + 1,
                    requests.get(i).getOffer(), requests.get(i).getNode()));
        }
        if (interactive) {
            System.out.println("Enter the number of a node to accept offer (anything else to ignore all offers): ");
            String input = in.nextLine();
            try {
                int choice = Integer.parseInt(input);
                if (choice > 0 && choice <= requests.size()) {
                    acceptOffer(filename, choice);
                }
            } catch (NumberFormatException e) {

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

    private void acceptOffer(String filename, int choice) {
        //TODO: Send to a real node based on @choice
        parent.sendMessage(Node.STUB, new AcceptBuyMessage(parent.getName(), filename));
    }

}
