package logic;

import files.File;
import messages.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Created by shambala on 02.04.17.
 */
public class BuyerLogic {
    HashMap<String, File> files = new HashMap<>();
    ArrayList<File> requested = new ArrayList<>();
    HashMap<String, Node> fileNodes = new HashMap<>();
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


    public void wantToBuy(Node seller, String fileToBuy, int price) {
        if (requested.stream().map(x -> x.getName()).collect(Collectors.toList()).contains(fileToBuy)) {
            logger.warn("File {} was already requested", fileToBuy);
            return;
        }
        RequestBuyMessage request = new RequestBuyMessage("name", price, fileToBuy);
        parent.sendMessage(request);
        requested.add(new File(fileToBuy, price));
        fileNodes.put(fileToBuy, seller);
    }

    public void onMessageReceived(Message message) {
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
                    parent.sendMessage(new HaveMoneyMessage(fileNodes.get(acceptedFile.getName()).getName(), acceptedFile));
                    waitFiles.add(acceptedFile.getName());
                } else {
                    parent.sendMessage(new BrokeMessage(fileNodes.get(acceptedFile.getName()).getName(), acceptedFile));
                }
                requested.remove(acceptedFile);
                fileNodes.remove(acceptedFile.getName());
            }
        }
        if (message instanceof TransferFileMessage) {
            File file = ((TransferFileMessage) message).getFile();
            if (!waitFiles.contains(file.getName())) {
                logger.warn("File {} was transferred, but wasn't requested", file.getName());
            } else {
                waitFiles.remove(file.getName());
                parent.getCurrentState().addDocument(file);
            }
        }
    }

    public ArrayList<File> printAvailableFiles() {
        return new ArrayList<>();
    }
}

