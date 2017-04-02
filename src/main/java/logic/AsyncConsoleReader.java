package logic;

/**
 * Created by amir.
 */

import java.io.InputStream;
import java.util.Scanner;

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

    public AsyncConsoleReader(SellerLogic sellerLogic) {
        this(sellerLogic, System.in);
    }

    public AsyncConsoleReader(SellerLogic sellerLogic, InputStream input) {
        this.sellerLogic = sellerLogic;
        this.input = input;
    }

    @Override
    public void run() {
        Scanner in = new Scanner(input);
        while (true) {
            String[] request = in.nextLine().split(" ");
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
                    System.exit(0);//kek
                default:
                    System.out.println("Can't parse your request");
            }
        }
    }
}
