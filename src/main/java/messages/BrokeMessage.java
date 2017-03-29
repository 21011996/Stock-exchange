package messages;

import files.File;

/**
 * @author ilya2
 *         created on 29.03.2017
 */
public class BrokeMessage extends Message {
    private File file;

    public BrokeMessage(String name, File file) {
        super(name);
        this.file = file;
    }

    public BrokeMessage(String name) {
        super(name);
    }

    //format : filename=price
    public static BrokeMessage parseRecord(Record record) {
        return new BrokeMessage(record.getName(), File.parseFile(record.getMessage()));
    }

    public Record toRecord() {
        return new Record(getName(), MessageType.BROKE.toString(), file.toString());
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
