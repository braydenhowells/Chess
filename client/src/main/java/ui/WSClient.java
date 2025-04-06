package ui;

import javax.websocket.*;
import java.net.URI;
import java.util.Scanner;

@ClientEndpoint // this tells java this class is a websocket client
public class WSClient {

    public static void main(String[] args) throws Exception {
        // creates a websocket container to manage connection
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        // connects this class to the ws server at the given uri
        container.connectToServer(WSClient.class, URI.create("ws://localhost:8080/ws"));
        // this line ^ does the 'upgrade' to WS from http

        // input loop for sending messages to the server
        Scanner scanner = new Scanner(System.in);
        System.out.println("enter a message to send to server:");

        while (true) {
            String input = scanner.nextLine();
            if (session != null && session.isOpen()) {
                try { // java suggests a try catch here
                    session.getBasicRemote().sendText(input);
                } catch (Exception e) {
                    e.printStackTrace();
                    break; // optional: break if we lose the connection
                }
            }
        }

    }

    private static Session session;

    @OnOpen
    public void onOpen(Session session) {
        // stores the session so we can reuse it
        WSClient.session = session;
        System.out.println("connected to websocket server");

        // automatically send a hello message on connect
        try {
            session.getBasicRemote().sendText("client has connected!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(String message) {
        // pass any incoming server messages to the facade
        WSServerFacade.handleServerMessage(message);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("connection closed: " + closeReason);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("websocket error: ");
        throwable.printStackTrace();
    }
}
