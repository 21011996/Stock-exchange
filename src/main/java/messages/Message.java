package messages;

/**
 * @author ilya2
 *         created on 29.03.2017
 */
public abstract class Message {
    public final String name;

    public Message(String name) {
        this.name = name;
    }

    public static Message parseRecord(Record record) {
        MessageType messageType = MessageType.parse(record.getHeader());
        switch (messageType) {
            case HELLO:
                return HelloMessage.parseRecord(record);
            case REQUEST_BUY:
                return RequestBuyMessage.parseRecord(record);
            case REJECT_BUY:
                return RejectBuyMessage.parseRecord(record);
            case ACCEPT_BUY:
                return AcceptBuyMessage.parseRecord(record);
            case HAVE_MONEY:
                return HaveMoneyMessage.parseRecord(record);
            case BROKE:
                return BrokeMessage.parseRecord(record);
            case TRANSFER_FILE:
                return TransferFileMessage.parseRecord(record);
            case NOTIFY_BUY:
                return NotifyBuyMessage.parseRecord(record);
            default:
                return null;
        }
    }

    public String getName() {
        return name;
    }

    public abstract Record toRecord();
}
