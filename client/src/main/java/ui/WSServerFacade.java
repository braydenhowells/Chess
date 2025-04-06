package ui;

public class WSServerFacade {

    // for now, just print what gets forwarded here

    public static void handleServerMessage(String msg) {
        System.out.println("client facade got message: " + msg);
    }
}
