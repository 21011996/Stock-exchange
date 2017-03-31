package messages;

/**
 * @author ilya2
 *         created on 29.03.2017
 */
public class RejectBuyMessage extends SellerMessages {
    private String fileName;
    private String reason;

    public RejectBuyMessage(String name, String fileName, String reason) {
        super(name);
        this.fileName = fileName;
        this.reason = reason;
    }

    public RejectBuyMessage(String name) {
        super(name);
    }

    //format : fileName,reason
    public static RejectBuyMessage parseRecord(Record record) {
        RejectBuyMessage answer = new RejectBuyMessage(record.getName());

        String[] filAndReason = record.getMessage().split(",");
        answer.setFileName(filAndReason[0]);
        answer.setReason(filAndReason[1]);
        return answer;
    }

    public Record toRecord() {
        return new Record(getName(), MessageType.REJECT_BUY.toString(), fileName + "," + reason);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
