package messages;

/**
 * @author ilya2
 *         created on 29.03.2017
 */
public class RequestBuyMessage extends Message {
    private int requestPrice;
    private String requestFile;

    public RequestBuyMessage(String name, int requestPrice, String requestFile) {
        super(name);
        this.requestPrice = requestPrice;
        this.requestFile = requestFile;
    }

    public RequestBuyMessage(String name) {
        super(name);
    }

    //Request message format : fileName,price
    public static RequestBuyMessage parseRecord(Record record) {
        RequestBuyMessage requestBuyMessage = new RequestBuyMessage(record.getName());

        String[] fileAndPrice = record.getMessage().split(",");
        requestBuyMessage.setRequestFile(fileAndPrice[0]);
        requestBuyMessage.setRequestPrice(Integer.parseInt(fileAndPrice[1]));

        return requestBuyMessage;
    }

    public int getRequestPrice() {

        return requestPrice;
    }

    public void setRequestPrice(int requestPrice) {
        this.requestPrice = requestPrice;
    }

    public String getRequestFile() {
        return requestFile;
    }

    public void setRequestFile(String requestFile) {
        this.requestFile = requestFile;
    }

    public Record toRecord() {
        return new Record(getName(), MessageType.REQUEST_BUY.toString(), requestFile + "," + requestPrice);
    }
}
