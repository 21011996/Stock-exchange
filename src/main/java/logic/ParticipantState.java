package logic;

import files.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class ParticipantState {
    Logger logger = LoggerFactory.getLogger(ParticipantState.class);
    private int balance;
    private HashMap<String, File> documents;
    private HashMap<String, File> remoteDocuments = new HashMap<>();
    private HashMap<String, String> documentNodes = new HashMap<>();
    private HashSet<String> neighbors = new HashSet<>();

    public ParticipantState(int balance, HashMap<String, File> documents) {
        this.balance = balance;
        this.documents = documents;
    }

    public HashSet<String> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(HashSet<String> neighbors) {
        this.neighbors = neighbors;
    }

    public HashMap<String, File> getDocuments() {
        return documents;
    }

    public void setDocuments(HashMap<String, File> documents) {
        this.documents = documents;
    }

    public void addDocuments(Collection<File> collection) {
        for (File file : collection) {
            addDocument(file);
        }
    }

    public void addDocument(File file) {
        if (documents.containsKey(file.getName())) {
            if (!documents.get(file.getName()).equals(file)) {
                logger.error("Filename uniqueness violated");
            } else {
                logger.warn("Repetitive addition of the same file detected");
            }
        } else {
            documents.put(file.getName(), file);
        }
    }

    public File getDocument(String filename) {
        return documents.get(filename);
    }


    public void addRemoteDocument(File file, String owner) {
        if (remoteDocuments.containsKey(file.getName())) {
            logger.error(">1 file's owner");
        } else {
            remoteDocuments.put(file.getName(), file);
            documentNodes.put(file.getName(), owner);
        }
    }

    public File getRemoteDocument(String fileName) {
        return remoteDocuments.get(fileName);
    }

    public HashMap<String, File> getRemoteDocuments() {
        return remoteDocuments;
    }

    public void setRemoteDocument(File file, String owner) {
        remoteDocuments.put(file.getName(), file);
        documentNodes.put(file.getName(), owner);
    }

    public void removeRemoteDocument(String filename) {
        remoteDocuments.remove(filename);
        documentNodes.remove(filename);
    }

    public String getDocumentNode(String filename) {
        return documentNodes.get(filename);
    }


    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void printRemote() {
        System.out.println("Available files:");
        for (File file : remoteDocuments.values()) {
            System.out.println(file.getName() + ": " + file.getPrice() + " at " + documentNodes.get(file.getName()));
        }
    }

    @Override
    public String toString() {
        return String.format("Balance: %s; Files: %s", balance, Arrays.toString(documents.values().toArray()));
    }
}
