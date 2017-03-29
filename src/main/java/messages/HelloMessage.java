package messages;

import files.File;

import java.util.ArrayList;

/**
 * @author ilya2
 *         created on 29.03.2017
 */
public class HelloMessage extends Message {
    private int balance;
    private ArrayList<File> files;

    public HelloMessage(String name, int balance, ArrayList<File> files) {
        super(name);
        this.balance = balance;
        this.files = files;
    }

    private HelloMessage(String name) {
        super(name);
    }

    //Hello message format : balance;file1=price,file2=price,...
    public static HelloMessage parseRecord(Record record) {
        HelloMessage answer = new HelloMessage(record.getName());

        String[] balanceAndFiles = record.getMessage().split(";");
        answer.setBalance(Integer.parseInt(balanceAndFiles[0]));

        ArrayList<File> files = new ArrayList<File>();
        for (String s : balanceAndFiles[1].split(",")) {
            files.add(File.parseFile(s));
        }
        answer.setFiles(files);
        return answer;
    }

    public Record toRecord() {
        StringBuilder s = new StringBuilder();
        s.append(balance);
        s.append(";");
        for (File f : files) {
            s.append(f.toString());
            s.append(",");
        }
        return new Record(getName(), MessageType.HELLO.toString(), s.toString());
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public ArrayList<File> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<File> files) {
        this.files = files;
    }
}
