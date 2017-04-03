package messages;

/**
 * @author ilya2
 *         created on 29.03.2017
 */
public enum MessageType {
    HELLO("hello"),
    REQUEST_BUY("request_buy"),
    REJECT_BUY("reject_buy"),
    ACCEPT_BUY("accept_buy"),
    HAVE_MONEY("have_money"),
    BROKE("broke"),
    TRANSFER_FILE("transfer_file"),
    NOTIFY_BUY("notify_buy"),
    HANDSHAKE_HELLO("handshake_hello"),
    HANDSHAKE_RESPONSE("handshake_response"),
    ERROR("error");

    private final String header;

    MessageType(String header) {
        this.header = header;
    }


    @Override
    public String toString() {
        return header;
    }

    public static MessageType parse(String header) {
        for (MessageType t : values()) {
            if (t.header.equals(header)) return t;
        }
        return ERROR;
    }

    public String header() {
        return header;
    }
}
