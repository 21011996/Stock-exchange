package logic;

/*
  Created by @amir.
 */

import com.sun.deploy.util.StringUtils;

import java.io.InputStream;
import java.util.Arrays;
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
    private final BuyerLogic buyerLogic;
    private final InputStream input;
    private final Node parent;

    public AsyncConsoleReader(Node parent, SellerLogic sellerLogic, BuyerLogic buyerLogic) {
        this(parent, sellerLogic, buyerLogic, System.in);
    }

    public AsyncConsoleReader(Node parent, SellerLogic sellerLogic, BuyerLogic buyerLogic, InputStream input) {
        this.parent = parent;
        this.sellerLogic = sellerLogic;
        this.buyerLogic = buyerLogic;
        this.input = input;
    }

    @Override
    public void run() {
        Thread.currentThread().setName(parent.getName() + "Console");
        Scanner in = new Scanner(input);
        while (!interrupted()) {
            String[] request = in.nextLine().replaceAll(" +", " ").split(" ");
            if (interrupted())
                break;
            //assuming that file names are one word
            switch (request[0]) {
                case "info":
                    if (request.length == 2) {
                        sellerLogic.printBidStatus(request[1]);
                    }
                    break;
                case "sell":
                    if (request.length == 3) {
                        sellerLogic.sellFile(request[1], request[2]);
                    }
                    break;
                case "infoall":
                    sellerLogic.printAllBids();
                    break;
                case "exit":
                    parent.shutdown();
                    Thread.currentThread().interrupt();
                    break;
                case "listfiles":
                    parent.getCurrentState().printRemote();
                    break;
                case "buy":
                    if (request.length == 3) {
                        buyerLogic.wantToBuy(request[1], Integer.parseInt(request[2]));
                    }
                    break;
                case "reject":
                    if (request.length >= 3) {
                        sellerLogic.rejectBid(request[1], request[2],
                                StringUtils.join(Arrays.asList(request).subList(3, request.length), " "));
                    }
                    break;
                case "status":
                    System.out.println(parent.getCurrentState().toString());
                    break;
                default:
                    System.out.println("Can't parse your request");
            }
        }
    }
}
