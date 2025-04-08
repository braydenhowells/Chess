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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class WSServerMailman {

    private final AuthService authService;
    private final GameService gameService;
    private final ConcurrentHashMap<String, Session> userToSession = new ConcurrentHashMap<>();
    // keeps ^^ track of ALL the sessions in our app. key = username, val = session
    private final ConcurrentHashMap<Session, String> sessionToUser = new ConcurrentHashMap<>();
    // reversal of our 1st map so we can easily find the user connected to a session
    private final ConcurrentHashMap<Integer, CopyOnWriteArrayList<String>> gameMembers = new ConcurrentHashMap<>();
    // keeps ^^ track of ALL the games currently attended by players / observers. key = gameID, val = username array
    private final ConcurrentHashMap<String, Integer> userToGame = new ConcurrentHashMap<>();
    // keeps ^^ track of the game a user is in. key = user, val = gameID
    // a user can be in here multiple times with different gameIDs for all the games they are in



    public WSServerMailman(AuthService authService, GameService gameService) {
        this.authService = authService;
        this.gameService = gameService;
    }

    public void onConnect(Session session) {
        System.out.println("a client connected");
    }

    private void handleLeave(Session session) {
        try {
            session.close(); // triggers onDisconnect
        } catch (Exception e) {
            e.printStackTrace(); // debug
            sendError(session, "leave command caused an error somehow");
        }
    }

    public void onDisconnect(Session session) {
        // do some actual sessions removal and make sure it dies
        System.out.println("a client disconnected"); // debug

        String dyingUser = sessionToUser.get(session);

        // send the leaving notification
        if (dyingUser != null) {
            Integer gameID = userToGame.get(dyingUser);
            ServerMessage msg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            msg.setMessage(dyingUser + " left the game");
            broadcastMessage(gameID, msg, dyingUser);
        }

        if (dyingUser != null) {
            sessionToUser.remove(session); // take out of maps
            userToSession.remove(dyingUser);

            Integer gameID = userToGame.get(dyingUser);
            if (gameID != null) {
                CopyOnWriteArrayList<String> members = gameMembers.get(gameID);
                if (members != null) {

                    members.remove(dyingUser);
                }
            }
            userToGame.remove(dyingUser);
        }
    }


    // this gets called every time something is sent to the server
    public void onMessage(Session session, String message) {
        System.out.println("received message: " + message);

        try {
            Gson gson = new Gson();
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);

            // sort which type of message this is
            switch (command.getCommandType()) {
                case CONNECT -> handleConnect(session, command);
                case LEAVE -> handleLeave(session);
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
            userToSession.put(currentUsername, session);
            // add to reverse map
            sessionToUser.put(session, currentUsername);
            // remember what game we are attached to for easy delete
            userToGame.put(currentUsername, gameID);



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
            Session session = userToSession.get(username);
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
        msg.setErrorMessage("error: " + errorText); // format to pass the test case
        sendMessage(session, msg);
    }
}
