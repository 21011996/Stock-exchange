package model;

/**
 * Created by heat_wave on 30.03.17.
 */
public class PurchaseRequest {
    private String node;
    private int offer;

    public PurchaseRequest(String node, int offer) {
        this.node = node;
        this.offer = offer;
    }

    public String getNode() {
        return node;
    }

    public int getOffer() {
        return offer;
    }
}
