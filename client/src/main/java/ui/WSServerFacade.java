package ui;

public class WSServerFacade {
// this class handles logic when messages come in from the server

    public static void handleServerMessage(String msg) {
        // just print the message for now
        System.out.println("client facade got message: " + msg);
    }
}
