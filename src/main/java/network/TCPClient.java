package network;

/**
 * Created by kirill on 03.04.17.
 */

import files.File;
import messages.BrokeMessage;
import messages.Message;

import java.io.*;
import java.net.*;

class TCPClient {
    public static void main(String argv[]) throws Exception {
        while (true) {
            String sentence;
            String modifiedSentence;
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            Socket clientSocket = new Socket(InetAddress.getByName("192.168.1.200"), 6789);
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            /*sentence = inFromUser.readLine();
            if (sentence.equals("end")) {
                clientSocket.close();
                break;
            }*/
            BrokeMessage msg = new BrokeMessage("name", new File("file", 239));
            //byte[] bytes = msg.toRecord().toByteBuffer();
            //outToServer.writeBytes(bytes);
            //clientSocket.getOutputStream().write(bytes);
            //clientSocket.getOutputStream().flush();
            //modifiedSentence = inFromServer.readLine();
            //System.out.println("FROM SERVER: " + modifiedSentence);
            clientSocket.close();
            break;
        }
    }
}

