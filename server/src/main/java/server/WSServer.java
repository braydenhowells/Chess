package server;

import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;

@WebSocket
public class WSServer {

    private static WSServerMailman mailman;

    public static void setMailman(WSServerMailman instance) {
        mailman = instance;
    }

    // so these show up as gray because we do not call them manually, but jetty uses them still
    @OnWebSocketConnect
    public void onConnect(Session session) {
        mailman.onConnect(session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        mailman.onMessage(session, message);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        mailman.onDisconnect(session);
    }
}
