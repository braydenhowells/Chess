package server;

import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import spark.Spark;

@WebSocket
public class WSServer {

    public static void main(String[] args) {
        Spark.port(8080);
        Spark.webSocket("/ws", WSServer.class);
        Spark.init();
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("client connected");
        try {
            session.getRemote().sendString("hi from ws server 👋");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        System.out.println("server got: " + message);
        try {
            session.getRemote().sendString("echo from server: " + message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("client disconnected");
    }
}
