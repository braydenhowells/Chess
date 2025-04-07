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
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class WSServerMailman {

    private final AuthService authService;
    private final GameService gameService;
    private final ConcurrentHashMap<String, Session> activeSessions = new ConcurrentHashMap<>();
    // keeps ^^ track of ALL the sessions in our app. key = username, val = session
    private final ConcurrentHashMap<Integer, CopyOnWriteArrayList<String>> gameMembers = new ConcurrentHashMap<>();
    // keeps ^^ track of ALL the games currently attended by players / observers. key = gameID, val = username set



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

            // add the session to our map after we know that sucker is legit
            String currentUsername = authData.username();
            activeSessions.put(currentUsername, session);

            // add user to the list of game members
            CopyOnWriteArrayList<String> members = gameMembers.get(gameID);
            if (members == null) {
                members = new CopyOnWriteArrayList<>();
                gameMembers.put(gameID, members);
            }
            if (!members.contains(currentUsername)) { // make sure that we do not already have this user
                members.add(currentUsername); // not having this check makes some tests fail
            }


            // get game
            GameData gameData = gameService.findGame(String.valueOf(gameID));
            if (gameData == null) {
                sendError(session, "game not found");
                return;
            }

            // send LOAD_GAME to the root client
            ServerMessage loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            loadGame.setGame(gameData);
            System.out.println("sending LOAD_GAME to client:"); // debug stuff
            System.out.println(new Gson().toJson(loadGame)); // debug stuff
            sendMessage(session, loadGame);

            // build NOTIFICATION for other members in game (if they exist)
            ServerMessage joinNoti = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);

            if (currentUsername.equals(gameData.whiteUsername())) {
                joinNoti.setMessage(currentUsername + " joined as WHITE");
            } else if (currentUsername.equals(gameData.blackUsername())) {
                joinNoti.setMessage(currentUsername + " joined as BLACK");
            } else {
                joinNoti.setMessage(currentUsername + " joined as OBSERVER");
            }

            // broadcast NOTIFICATION yessah
            System.out.println("broadcasting notification:"); // debug stuff
            System.out.println(new Gson().toJson(joinNoti)); // debug stuff
            broadcastMessage(gameID, joinNoti, currentUsername);

        } catch (Exception e) {
            e.printStackTrace();
            sendError(session, "internal server error");
        }
    }

    // method for sending messages to all users in a game except 1
    private void broadcastMessage(int gameID, ServerMessage message, String excludeThisUser) {
        CopyOnWriteArrayList<String> members = gameMembers.get(gameID);
        if (members == null) return;

        for (String username : members) {
            if (Objects.equals(username, excludeThisUser)) {
                continue;
            }
            Session session = activeSessions.get(username);
            if (session != null && session.isOpen()) {
                sendMessage(session, message);
            }
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
