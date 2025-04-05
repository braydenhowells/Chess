import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import spark.Spark;

@WebSocket
public class WSServer {
    public static void main(String[] args) {
        Spark.port(8080); // startup server on 8080
        Spark.webSocket("/ws", WSServer.class); // when someone uses ws path, upgrade them to a websocket
        Spark.get("/echo/:msg", (req, res) -> "HTTP response: " + req.params(":msg"));
    }

    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
        System.out.println("A client connected!");
        session.getRemote().sendString("Welcome! You are connected to the WebSocket server.");
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        System.out.printf("Received: %s", message);
        session.getRemote().sendString("WebSocket response: " + message);
    }
}