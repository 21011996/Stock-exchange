package messages;

import files.File;

/**
 * @author ilya2
 *         created on 29.03.2017
 */
public class TransferFileMessage extends Message {
    private File file;

    public TransferFileMessage(String name, File file) {
        super(name);
        this.file = file;
    }

    public TransferFileMessage(String name) {
        super(name);
    }

    //format : filename=price
    public static TransferFileMessage parseRecord(Record record) {
        return new TransferFileMessage(record.getName(), File.parseFile(record.getMessage()));
    }

    public Record toRecord() {
        return new Record(getName(), MessageType.TRANSFER_FILE.toString(), file.toString());
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
