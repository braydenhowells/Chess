package server;

import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import spark.Spark;

@WebSocket // marks this class as a websocket endpoint
public class WSServer {

    public static void main(String[] args) {
        // sets the port the server will listen on
        Spark.port(8080);

        // registers the websocket handler class at path /ws
        Spark.webSocket("/ws", WSServer.class);

        // starts the server
        Spark.init();
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        // logs when a client connects and sends a welcome message
        System.out.println("client connected");
        try {
            session.getRemote().sendString("hi from ws server 👋");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        // logs incoming message and echoes it back
        System.out.println("server got: " + message);
        try {
            session.getRemote().sendString("echo from server: " + message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        // logs when a client disconnects
        System.out.println("client disconnected");
    }
}
