package logic;

import files.File;
import messages.RequestBuyMessage;

import java.util.*;

public class ParticipantState {
    private int balance;
    private List<File> documents;
    private Set<RequestBuyMessage> requestLocks;
    private Map<File, Set<RequestBuyMessage>> auction;

    public ParticipantState(int balance, List<File> documents,
                            Set<RequestBuyMessage> requestLocks, Map<File, Set<RequestBuyMessage>> auction) {
        this.balance = balance;
        this.documents = documents;
        this.requestLocks = requestLocks;
        this.auction = auction;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public List<File> getDocuments() {
        return documents;
    }

    public void setDocuments(List<File> documents) {
        this.documents = documents;
    }

    public Set<RequestBuyMessage> getRequestLocks() {
        return requestLocks;
    }

    public void setRequestLocks(Set<RequestBuyMessage> requestLocks) {
        this.requestLocks = requestLocks;
    }

    public Map<File, Set<RequestBuyMessage>> getAuction() {
        return auction;
    }

    public void setAuction(Map<File, Set<RequestBuyMessage>> auction) {
        this.auction = auction;
    }
}
