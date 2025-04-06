package ui;

import javax.websocket.*;
import java.net.URI;
import java.util.Scanner;

@ClientEndpoint
public class WSClient {

    public static void main(String[] args) throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(WSClient.class, URI.create("ws://localhost:8080/ws"));

        Scanner scanner = new Scanner(System.in);
        System.out.println("enter a message to send to server:");
        while (true) {
            String input = scanner.nextLine();
            if (session != null && session.isOpen()) {
                session.getBasicRemote().sendText(input);
            }
        }
    }

    private static Session session;

    @OnOpen
    public void onOpen(Session session) {
        WSClient.session = session;
        System.out.println("connected to websocket server");
        try {
            session.getBasicRemote().sendText("client has connected!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(String message) {
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
