package logic;

import files.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;

public class ParticipantState {
    private int balance;
    private HashMap<String, File> documents;
    Logger logger = LoggerFactory.getLogger(ParticipantState.class);

    public ParticipantState(int balance, HashMap<String, File> documents) {
        this.balance = balance;
        this.documents = documents;
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


    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

}
