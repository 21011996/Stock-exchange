package messages;

import java.nio.ByteBuffer;

/**
 * @author ilya2
 *         created on 29.03.2017
 */
public class Record {
    private String name;
    private String header;
    private String message;

    public Record(String name, String header, String message) {
        this.name = name;
        this.header = header;
        this.message = message;
    }

    public static Record fromByteBuffer(byte[] buffer) {
        /*String name = stringFromBB(buffer);
        String header = stringFromBB(buffer);
        String message = stringFromBB(buffer);*/
        int nameLenght = buffer[0];
        StringBuilder name = new StringBuilder();
        int i = 1;
        for (int j = i; j < nameLenght + 1; j++) {
            name.append((char) buffer[j]);
        }
        i += nameLenght;
        int headerLenght = buffer[i];
        StringBuilder header = new StringBuilder();
        for (int j = i + 1; j < headerLenght + 1 + i; j++) {
            header.append((char) buffer[j]);
        }
        i += headerLenght + 1;
        int messageLenght = buffer[i];
        StringBuilder message = new StringBuilder();
        for (int j = i + 1; j < messageLenght + 1 + i; j++) {
            message.append((char) buffer[j]);
        }
        return new Record(name.toString(), header.toString(), message.toString());
    }

    private static String stringFromBB(ByteBuffer buffer) {
        byte length = buffer.get();
        if (length != 0) {
            StringBuilder answer = new StringBuilder();
            for (int i = 0; i < length; i++) {
                answer.append((char) buffer.get());
            }
            return answer.toString();
        } else {
            return "";
        }
    }

    public byte[] toByteBuffer() {
        byte[] answer = new byte[name.length() + 1 + header.length() + 1 + message.length() + 1];
        int i = 0;
        answer[i] = (byte) name.length();
        for (int j = 0; j < name.length(); j++) {
            answer[j + i + 1] = (byte) name.charAt(j);
        }
        i += name.length() + 1;
        answer[i] = (byte) header.length();
        for (int j = 0; j < header.length(); j++) {
            answer[j + i + 1] = (byte) header.charAt(j);
        }
        i += header.length() + 1;
        answer[i] = (byte) message.length();
        for (int j = 0; j < message.length(); j++) {
            answer[j + i + 1] = (byte) message.charAt(j);
        }

        return answer;
    }

    private ByteBuffer stringToBB(String s) {
        ByteBuffer buffer = ByteBuffer.allocate(2048);
        buffer.put((byte) s.length());
        for (char c : s.toCharArray()) {
            buffer.put((byte) c);
        }
        return (ByteBuffer) buffer.flip();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Record record = (Record) o;
        if (!this.getName().equals(record.getName())) return false;
        if (!this.getHeader().equals(record.getHeader())) return false;
        return this.getMessage().equals(record.getMessage());
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + header.hashCode();
        result = 31 * result + message.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("name = %s, header = %s, message = %s", name, header, message);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
