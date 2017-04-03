//package messages;
//
//import java.nio.ByteBuffer;
//
///**
// * @author ilya2
// *         created on 29.03.2017
// */
//public class Record {
//    private String name;
//    private String header;
//    private String message;
//
//    public Record(String name, String header, String message) {
//        this.name = name;
//        this.header = header;
//        this.message = message;
//    }
//
//    public static Record fromByteBuffer(byte[] buffer) {
//        /*String name = stringFromBB(buffer);
//        String header = stringFromBB(buffer);
//        String message = stringFromBB(buffer);*/
//        int nameLenght = buffer[0];
//        StringBuilder name = new StringBuilder();
//        int i = 1;
//        for (int j = i; j < nameLenght + 1; j++) {
//            name.append((char) buffer[j]);
//        }
//        i += nameLenght;
//        int headerLenght = buffer[i];
//        StringBuilder header = new StringBuilder();
//        for (int j = i + 1; j < headerLenght + 1 + i; j++) {
//            header.append((char) buffer[j]);
//        }
//        i += headerLenght + 1;
//        int messageLenght = buffer[i];
//        StringBuilder message = new StringBuilder();
//        for (int j = i + 1; j < messageLenght + 1 + i; j++) {
//            message.append((char) buffer[j]);
//        }
//        return new Record(name.toString(), header.toString(), message.toString());
//    }
//
//
//    public byte[] toByteBuffer() {
//        byte[] answer = new byte[name.length() + 1 + header.length() + 1 + message.length() + 1];
//        int i = 0;
//        answer[i] = (byte) name.length();
//        for (int j = 0; j < name.length(); j++) {
//            answer[j + i + 1] = (byte) name.charAt(j);
//        }
//        i += name.length() + 1;
//        answer[i] = (byte) header.length();
//        for (int j = 0; j < header.length(); j++) {
//            answer[j + i + 1] = (byte) header.charAt(j);
//        }
//        i += header.length() + 1;
//        answer[i] = (byte) message.length();
//        for (int j = 0; j < message.length(); j++) {
//            answer[j + i + 1] = (byte) message.charAt(j);
//        }
//
//        return answer;
//    }
//
//}
