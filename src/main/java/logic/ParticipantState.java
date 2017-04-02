package logic;

import files.File;

import java.util.HashMap;

public class ParticipantState {
    private int balance;
    private HashMap<String, File> documents;

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

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

}
