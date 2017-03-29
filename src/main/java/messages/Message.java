package messages;

import java.nio.ByteBuffer;

/**
 * @author ilya2
 *         created on 29.03.2017
 */
public class Message {
    private String name;
    private String header;
    private String message;

    public Message(String name, String header, String message) {
        this.name = name;
        this.header = header;
        this.message = message;
    }

    public static Message fromByteBuffer(ByteBuffer buffer) {
        String name = stringFromBB(buffer);
        String header = stringFromBB(buffer);
        String message = stringFromBB(buffer);
        return new Message(name, header, message);
    }

    private static String stringFromBB(ByteBuffer buffer) {
        byte length = buffer.get();
        if (length != 0) {
            StringBuilder answer = new StringBuilder(63);
            for (int i = 0; i < length; i++) {
                answer.append((char) buffer.get());
            }
            return answer.toString();
        } else {
            return "";
        }
    }

    public ByteBuffer toByteBuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(2048);

        ByteBuffer nameBuffer = stringToBB(name);
        buffer.put(nameBuffer);

        ByteBuffer headerBuffer = stringToBB(header);
        buffer.put(headerBuffer);

        ByteBuffer messageBuffer = stringToBB(message);
        buffer.put(messageBuffer);

        return (ByteBuffer) buffer.flip();
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

        Message message = (Message) o;
        if (!this.getName().equals(message.getName())) return false;
        if (!this.getHeader().equals(message.getHeader())) return false;
        return this.getMessage().equals(message.getMessage());
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
