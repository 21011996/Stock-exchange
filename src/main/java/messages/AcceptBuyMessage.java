package messages;

/**
 * @author ilya2
 *         created on 29.03.2017
 */
public class AcceptBuyMessage extends Message {
    private String fileName;

    public AcceptBuyMessage(String name, String fileName) {

        super(name);
        this.fileName = fileName;
    }

    public AcceptBuyMessage(String name) {
        super(name);
    }

    // format : fileName
    public static AcceptBuyMessage parseRecord(Record record) {
        return new AcceptBuyMessage(record.getName(), record.getMessage());
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Record toRecord() {
        return new Record(getName(), MessageType.ACCEPT_BUY.toString(), getFileName());
    }
}
