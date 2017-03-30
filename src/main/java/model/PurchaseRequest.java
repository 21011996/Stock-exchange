package model;

import logic.Node;

/**
 * Created by heat_wave on 30.03.17.
 */
public class PurchaseRequest {
    private Node node;
    private int offer;

    public PurchaseRequest(Node node, int offer) {
        this.node = node;
        this.offer = offer;
    }

    public Node getNode() {

        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public int getOffer() {
        return offer;
    }

    public void setOffer(int offer) {
        this.offer = offer;
    }
}
