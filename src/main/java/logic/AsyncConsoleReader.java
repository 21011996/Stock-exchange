package logic;

/**
 * Created by amir.
 */

import java.io.InputStream;
import java.util.Scanner;

import static java.lang.Thread.interrupted;

/**
 * Class to interact with console.
 * info "filename" - returns information about file
 * sell "filename" "username" - sells file to user
 * infoall - returns information about all files
 * exit - terminates program
 */
public class AsyncConsoleReader implements Runnable {

    private final SellerLogic sellerLogic;
    private final InputStream input;
    private final Node parent;

    public AsyncConsoleReader(Node parent, SellerLogic sellerLogic) {
        this(parent, sellerLogic, System.in);
    }

    public AsyncConsoleReader(Node parent, SellerLogic sellerLogic, InputStream input) {
        this.parent = parent;
        this.sellerLogic = sellerLogic;
        this.input = input;
    }

    @Override
    public void run() {
        Thread.currentThread().setName(parent.getName() + "Console");
        Scanner in = new Scanner(input);
        while (!interrupted()) {
            String[] request = in.nextLine().split(" ");
            if (interrupted())
                break;
            //assuming that file names are one word
            switch (request[0]) {
                case "info":
                    sellerLogic.printBidStatus(request[1]);
                    break;
                case "sell":
                    sellerLogic.sellFile(request[1], request[2]);
                    break;
                case "infoall":
                    sellerLogic.printAllBids();
                    break;
                case "exit":
                    parent.shutdown();
                    Thread.currentThread().interrupt();
                    break;
                //TODO add cases for buyer logic
                default:
                    System.out.println("Can't parse your request");
            }
        }
    }
}
