package messages;

import files.File;

/**
 * @author ilya2
 *         created on 29.03.2017
 */
public class NotifyBuyMessage extends Message {
    private File file;

    public NotifyBuyMessage(String name, File file) {
        super(name);
        this.file = file;
    }

    public NotifyBuyMessage(String name) {
        super(name);
    }

    //format : filename=price
    public static NotifyBuyMessage parseRecord(Record record) {
        return new NotifyBuyMessage(record.getName(), File.parseFile(record.getMessage()));
    }

    public Record toRecord() {
        return new Record(getName(), MessageType.NOTIFY_BUY.toString(), file.toString());
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
