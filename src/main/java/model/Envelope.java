package model;

import messages.Message;

/**
 * @author ilya2
 *         created on 03.04.2017
 */
public class Envelope {
    private String destination;
    private Message message;

    public Envelope(String destination, Message message) {
        this.destination = destination;
        this.message = message;
    }

    public String getDestination() {
        return destination;
    }

    public Message getMessage() {
        return message;
    }
}
