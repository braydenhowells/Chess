package server;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import service.AuthService;
import service.GameService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WSServerMailman {

    private final AuthService authService;
    private final GameService gameService;

    // track all active sessions
    private final Set<Session> sessions = ConcurrentHashMap.newKeySet();

    public WSServerMailman(AuthService authService, GameService gameService) {
        this.authService = authService;
        this.gameService = gameService;
    }

    public void onConnect(Session session) {
        sessions.add(session);
        System.out.println("a client connected");
    }

    public void onDisconnect(Session session) {
        sessions.remove(session);
        System.out.println("a client disconnected");
    }

    public void onMessage(Session session, String message) {
        System.out.println("received message: " + message);

        try {
            Gson gson = new Gson();
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);

            switch (command.getCommandType()) {
                case CONNECT -> handleConnect(session, command);
                default -> sendError(session, "unrecognized command type: " + command.getCommandType());
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendError(session, "invalid message format");
        }
    }

    private void handleConnect(Session session, UserGameCommand command) {
        try {
            String authToken = command.getAuthToken();
            int gameID = command.getGameID();

            // validate auth
            AuthData authData = authService.getAuthData(authToken);
            if (authData == null) {
                sendError(session, "unauthorized");
                return;
            }

            String username = authData.username();

            // get game
            GameData gameData = gameService.findGame(String.valueOf(gameID));
            if (gameData == null) {
                sendError(session, "game not found");
                return;
            }

            // check if player or observer
            boolean isPlayer = username.equals(gameData.whiteUsername()) || username.equals(gameData.blackUsername());
            boolean isObserver = !isPlayer;

            if (!isPlayer && !isObserver) {
                sendError(session, "you are not part of this game");
                return;
            }

            // send game back
            ServerMessage response = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            response.setGame(gameData);
            // debug
            System.out.println("sending LOAD_GAME to client:");
            System.out.println(new Gson().toJson(response));

            sendMessage(session, response);

        } catch (Exception e) {
            e.printStackTrace();
            sendError(session, "internal server error");
        }
    }

    private void sendMessage(Session session, ServerMessage msg) {
        try {
            String json = new Gson().toJson(msg);
            session.getRemote().sendString(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendError(Session session, String errorText) {
        ServerMessage msg = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
        msg.setMessage("error: " + errorText);
        sendMessage(session, msg);
    }
}
