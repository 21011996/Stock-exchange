package messages;

import files.File;

/**
 * @author ilya2
 *         created on 29.03.2017
 */
public class HaveMoneyMessage extends BuyerMessages {
    private File file;

    public HaveMoneyMessage(String name, File file) {
        super(name);
        this.file = file;
    }

    public HaveMoneyMessage(String name) {
        super(name);
    }

    //format : filename=price
    public static HaveMoneyMessage parseRecord(Record record) {
        return new HaveMoneyMessage(record.getName(), File.parseFile(record.getMessage()));
    }

    public Record toRecord() {
        return new Record(getName(), MessageType.HAVE_MONEY.toString(), file.toString());
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
