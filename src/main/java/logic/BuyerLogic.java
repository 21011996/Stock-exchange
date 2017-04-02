package logic;

import files.File;
import messages.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by shambala on 02.04.17.
 */
public class BuyerLogic {
    HashMap<String, File> files = new HashMap<>();
    ArrayList<File> requested = new ArrayList<>();
    HashMap<String, Node> fileNodes = new HashMap<>();
    ArrayList<String> waitFiles = new ArrayList<>();
    ArrayList<Node> remoteNodes = new ArrayList<>();

    int balance;

    Node parent;

    BuyerLogic(Node parent, int balance) {
        this.parent = parent;
        this.balance = balance;
    }

    BuyerLogic(Node parent, int balance, Collection<File> files) {
        this(parent, balance);
        addFiles(files);
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

    public void wantToBuy(Node seller, String fileToBuy, int price) {
        if (requested.contains(fileToBuy)) {
            Logger.getGlobal().log(Level.WARNING, "File " + fileToBuy+ " was already requested");
            return;
        }
        RequestBuyMessage request = new RequestBuyMessage("name", price, fileToBuy);
        parent.sendMessage(seller.getName(), request);
        requested.add(new File(fileToBuy, price));
        fileNodes.put(fileToBuy, seller);
    }

    public void onReceiveMessage(Message message) {
        if (message instanceof AcceptBuyMessage) {
            String name = ((AcceptBuyMessage) message).getFileName();
            if (!requested.stream().map(x -> x.getName()).collect(Collectors.toList()).contains(name)) {
                Logger.getGlobal().log(Level.WARNING, "Accept without request");
            } else {
                File acceptedFile = null;
                for (File file : requested) {
                    if (file.getName().equals(name)) {
                        acceptedFile = file;
                        break;
                    }
                }
                if (acceptedFile.getPrice()<=balance) {
                    balance -= acceptedFile.getPrice();
                    parent.sendMessage(fileNodes.get(acceptedFile.getName()).getName(), new HaveMoneyMessage("name", acceptedFile));
                    waitFiles.add(acceptedFile.getName());
                } else {
                    parent.sendMessage(fileNodes.get(acceptedFile.getName()).getName(), new BrokeMessage("name", acceptedFile));
                }
                requested.remove(acceptedFile);
                fileNodes.remove(acceptedFile.getName());
            }
        }
        if (message instanceof TransferFileMessage) {
            File file = ((TransferFileMessage) message).getFile();
            if (!waitFiles.contains(file.getName())) {
                Logger.getGlobal().log(Level.WARNING, "File was transferred, but wasn't requested");
            } else {
                waitFiles.remove(file.getName());
                addFile(file);
                for (Node node : remoteNodes) {
                    parent.sendMessage(node.getName(), new NotifyBuyMessage("name", file));
                }
            }
        }
    }
}
