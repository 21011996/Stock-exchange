package logic;

import files.File;
import messages.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Created by shambala on 02.04.17.
 */
public class BuyerLogic {
    HashMap<String, File> files = new HashMap<>();
    ArrayList<File> requested = new ArrayList<>();
    ArrayList<String> waitFiles = new ArrayList<>();


    Logger logger = LoggerFactory.getLogger(BuyerLogic.class);

    Node parent;

    BuyerLogic(Node parent) {
        this.parent = parent;
        this.files = parent.getCurrentState().getDocuments();
    }

    BuyerLogic(Node parent, Collection<File> files) {
        this(parent);
        parent.getCurrentState().addDocuments(files);
    }


    public void wantToBuy(String fileToBuy, int price) {
        if (requested.stream().map(x -> x.getName()).collect(Collectors.toList()).contains(fileToBuy)) {
            logger.warn("File {} was already requested", fileToBuy);
            return;
        }
        RequestBuyMessage request = new RequestBuyMessage(parent.getName(), price, fileToBuy);
        String fileOwner = parent.getCurrentState().getDocumentNode(fileToBuy);
        if (fileOwner != null) {
            parent.sendMessage(parent.getCurrentState().getDocumentNode(fileToBuy), request);
            requested.add(new File(fileToBuy, price));
        } else {
            logger.error("Requested file is not available");
        }
    }

    public void onMessageReceived(Message message) {
        if (message instanceof HelloMessage) {
            for (File file : ((HelloMessage) message).getFiles()) {
                parent.getCurrentState().addRemoteDocument(file, message.getName());
                parent.getCurrentState().getNeighbors().add(message.getName());
            }
        }
        if (message instanceof NotifyBuyMessage) {
            if (!message.getName().equals(parent.getName())) {
                parent.getCurrentState().setRemoteDocument(((NotifyBuyMessage) message).getFile(), message.getName());
            }
        }
        if (message instanceof RejectBuyMessage) {
            System.out.println(String.format("Your bid for file %s was rejected by %s, reason: %s", ((RejectBuyMessage) message).getFileName(), message.getName(), ((RejectBuyMessage) message).getReason()));
        }
        if (message instanceof AcceptBuyMessage) {
            String name = ((AcceptBuyMessage) message).getFileName();
            if (!requested.stream().map(x -> x.getName()).collect(Collectors.toList()).contains(name)) {
                logger.warn("Accept without request");
            } else {
                File acceptedFile = null;
                for (File file : requested) {
                    if (file.getName().equals(name)) {
                        acceptedFile = file;
                        break;
                    }
                }
                if (acceptedFile.getPrice()<=parent.getCurrentState().getBalance()) {
                    parent.getCurrentState().setBalance(parent.getCurrentState().getBalance()-acceptedFile.getPrice());
                    parent.sendMessage(parent.getCurrentState().getDocumentNode(acceptedFile.getName()), new HaveMoneyMessage(parent.getName(), acceptedFile));
                    waitFiles.add(acceptedFile.getName());
                } else {
                    parent.sendMessage(parent.getCurrentState().getDocumentNode(acceptedFile.getName()), new BrokeMessage(parent.getName(), acceptedFile));
                }
                requested.remove(acceptedFile);
            }
        }
        if (message instanceof TransferFileMessage) {
            File file = ((TransferFileMessage) message).getFile();
            if (!waitFiles.contains(file.getName())) {
                 logger.warn("File {} was transferred, but wasn't requested", file.getName());
            } else {
                waitFiles.remove(file.getName());
                parent.getCurrentState().addDocument(file);
                parent.getCurrentState().removeRemoteDocument(file.getName());
            }
        }
    }
}