package ui;

import javax.websocket.*;
import java.net.URI;
import java.util.Scanner;

@ClientEndpoint // this tells java this class is a websocket client
public class WSClient {

    private static Session session;

    public static void main(String[] args) throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(WSClient.class, URI.create("ws://localhost:8080/ws"));
    }


    // sends a raw json string over the websocket
    public static void sendRaw(String json) {
        try {
            if (session != null && session.isOpen()) {
                session.getBasicRemote().sendText(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        WSClient.session = session;
        System.out.println("websocket connection established.");
    }

    @OnMessage
    public void onMessage(String message) {
        WSClientMailman.handleServerMessage(message);
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
