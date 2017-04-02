package network;

/**
 * Created by kirill on 03.04.17.
 */

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
            sentence = inFromUser.readLine();
            if (sentence.equals("end")) {
                clientSocket.close();
                break;
            }
            outToServer.writeBytes(sentence + '\n');
            modifiedSentence = inFromServer.readLine();
            System.out.println("FROM SERVER: " + modifiedSentence);
            clientSocket.close();
        }
    }
}

