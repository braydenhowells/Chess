package server;

import chess.ChessGame;
import chess.ChessPosition;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import requests.JoinRequest;
import service.AuthService;
import service.GameService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
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
    private final ConcurrentHashMap<String, String> userToAuthToken = new ConcurrentHashMap<>();
    // LAST map, used for leaving games with the auth token


    public WSServerMailman(AuthService authService, GameService gameService) {
        this.authService = authService;
        this.gameService = gameService;
    }

    public void onConnect(Session session) {
        System.out.println("a client connected");
    }

    private void handleLeave(Session session) {
        // get user
        String dyingUser = sessionToUser.get(session);

        // see if they are an observer (a null value)
        if (dyingUser == null) {
            try {
                session.close(); // just close it, no need to remove username from game
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        // get the gameID
        Integer gameID = userToGame.get(dyingUser);

        // use the gameID map to get the game data
        GameData gameData = gameService.findGame(String.valueOf(gameID));

        // get auth token
        String authToken = userToAuthToken.get(dyingUser);

        // sanity check
        if (gameData == null || authToken == null) {
            try {
                session.close(); // still try to close
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        // update db with null username
        // by calling http join from here (fake join = leave)
        if (dyingUser.equals(gameData.whiteUsername())) {
            gameService.join(gameData, "WHITE", authToken, new JoinRequest("WHITE", String.valueOf(gameID)));
        } else if (dyingUser.equals(gameData.blackUsername())) {
            gameService.join(gameData, "BLACK", authToken, new JoinRequest("BLACK", String.valueOf(gameID)));
        }
        // else they are an observer – no need to update the db

        // remove from websocket also
        try {
            session.close(); // triggers onDisconnect
        } catch (Exception e) {
            e.printStackTrace(); // debug
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
            userToAuthToken.remove(dyingUser);


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

                case MAKE_MOVE -> handleMove(command, session);

                case RESIGN -> handleResign(command, session);

                default -> sendError(session, "unrecognized command type: " + command.getCommandType());
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendError(session, "invalid message format");
        }
    }

    private void handleMove(UserGameCommand command, Session session) {
        String username = sessionToUser.get(session);
        Integer gameID = command.getGameID();
        var move = command.getMove();

        // check auth rq
        String authToken = command.getAuthToken();
        AuthData authData = authService.getAuthData(authToken);
        if (authData == null) {
            sendError(session, "Invalid or unauthorized auth token.");
            return;
        }

        if (username == null || gameID == null || move == null) {
            sendError(session, "Invalid move command or missing fields.");
            return;
        }

        // Fetch the game
        GameData gameData = gameService.findGame(String.valueOf(gameID));
        if (gameData == null) {
            sendError(session, "Game not found.");
            return;
        }

        // is game over?
        if (gameData.gameOver()) {
            sendError(session, "Invalid move: this game is already over");
            return;
        }

        ChessGame game = gameData.game();
        // make sure it is our turn to move
        ChessGame.TeamColor turn = game.getTeamTurn();
        String whitePlayer = gameData.whiteUsername();
        String blackPlayer = gameData.blackUsername();
        boolean isWhiteTurn = turn == ChessGame.TeamColor.WHITE;
        boolean isBlackTurn = turn == ChessGame.TeamColor.BLACK;
        boolean isWhiteUser = username.equals(whitePlayer);
        boolean isBlackUser = username.equals(blackPlayer);
        // stop the move if it is not ours
        if ((isWhiteTurn && !isWhiteUser) || (isBlackTurn && !isBlackUser)) {
            sendError(session, "It's not your turn.");
            return;
        }

        try {
            game.makeMove(move); // this will throw an exception if move is illegal
            // Persist updated game
            GameData updated = new GameData(
                    gameData.gameID(),
                    gameData.whiteUsername(),
                    gameData.blackUsername(),
                    gameData.gameName(),
                    game,
                    gameData.gameOver() // optionally update gameOver if handling that
            );
            gameService.updateGame(updated);

            // broadcast LOAD_GAME to all members
            ServerMessage msg = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            msg.setGame(updated);
            broadcastMessage(gameID, msg, null); // exclude nobody hehe

            // send NOTIFICATION to other players
            String moveText = username + " moved from " + unFormatPosition(move.getStartPosition()) +
                    " to " + unFormatPosition(move.getEndPosition());
            ServerMessage noti = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            noti.setMessage(moveText);
            // goes to all users except the one who moved
            broadcastMessage(gameID, noti, username);
            // broadcast check/mate/stale if we need to
            checkGameState(game, gameData, gameID);
        } catch (Exception e) {
            sendError(session, "Invalid move: " + e.getMessage());
        }
    }

    private void checkGameState(ChessGame game, GameData gameData, int gameID) {
        ChessGame.TeamColor nextTurn = game.getTeamTurn();

        String color = "";
        if (nextTurn == ChessGame.TeamColor.WHITE) {
            color = "White";
        } else if (nextTurn == ChessGame.TeamColor.BLACK) {
            color = "Black";
        }

        String inCheckUsername = "";
        if (nextTurn == ChessGame.TeamColor.WHITE) {
            inCheckUsername = gameData.whiteUsername();
        } else if (nextTurn == ChessGame.TeamColor.BLACK) {
            inCheckUsername = gameData.blackUsername();
        }

        ChessGame.TeamColor currentTurn;
        if (nextTurn == ChessGame.TeamColor.WHITE) {
            currentTurn = ChessGame.TeamColor.BLACK;
        } else {
            currentTurn = ChessGame.TeamColor.WHITE;
        }

        String winnerUsername = "";
        if (currentTurn == ChessGame.TeamColor.WHITE) {
            winnerUsername = gameData.whiteUsername();
        } else if (currentTurn == ChessGame.TeamColor.BLACK) {
            winnerUsername = gameData.blackUsername();
        }

        boolean isInCheckmate = game.isInCheckmate(nextTurn);
        if (isInCheckmate) {
            ServerMessage checkmateMsg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            checkmateMsg.setMessage("Checkmate! " + winnerUsername +
                    " (" + (currentTurn == ChessGame.TeamColor.WHITE ? "White" : "Black") + ") wins.");
            broadcastMessage(gameID, checkmateMsg, null);

            // mark game over
            try {
                gameService.updateGame(new GameData(
                        gameData.gameID(),
                        gameData.whiteUsername(),
                        gameData.blackUsername(),
                        gameData.gameName(),
                        game,
                        true // game now over
                ));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return;
        }

        boolean isStalemate = game.isInStalemate(nextTurn);
        if (isStalemate) {
            ServerMessage stalemateMsg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            stalemateMsg.setMessage("Stalemate! The game is a draw.");
            broadcastMessage(gameID, stalemateMsg, null);

            // mark game over
            try {
                gameService.updateGame(new GameData(
                        gameData.gameID(),
                        gameData.whiteUsername(),
                        gameData.blackUsername(),
                        gameData.gameName(),
                        game,
                        true
                ));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        boolean isInCheck = game.isInCheck(nextTurn);
        if (isInCheck) {
            ServerMessage checkMsg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            checkMsg.setMessage("Check! " + inCheckUsername + " (" + color + ") is in check.");
            broadcastMessage(gameID, checkMsg, null);
        }
    }


    private String unFormatPosition(ChessPosition pos) {
        int col = pos.getColumn();
        int row = pos.getRow();
        String file;
        String rank = String.valueOf(row);

        switch (col) {
            case 1 -> file = "a";
            case 2 -> file = "b";
            case 3 -> file = "c";
            case 4 -> file = "d";
            case 5 -> file = "e";
            case 6 -> file = "f";
            case 7 -> file = "g";
            case 8 -> file = "h";
            default -> file = "?"; // just in case?? forces a default anyway
        }
        return file + rank; // as a string
    }

    private void handleResign(UserGameCommand command, Session session) {
        String username = sessionToUser.get(session);
        int gameID = command.getGameID();
        GameData gameData = gameService.findGame(String.valueOf(gameID));

        // make sure game is not already over
        ChessGame game = gameData.game();
        if (gameData.gameOver()) {
            sendError(session, "Failed to resign, this game is already over.");
            return;
        }

        // check if the user is in the game
        // this will allow us to pass the resign test from observer
        List<String> players = List.of(gameData.blackUsername(), gameData.whiteUsername());
        if (!players.contains(username)) {
            sendError(session, "Failed to resign, you are an observer.");
            return;
        }

        // same game but gameOver = true
        GameData updated = new GameData(
                gameData.gameID(),
                gameData.whiteUsername(),
                gameData.blackUsername(),
                gameData.gameName(),
                game,
                true // gg
        );
        try {
            gameService.updateGame(updated);
        } catch (SQLException e) {
            sendError(session, "SQL error: " + e.getMessage());
        }

        // Notify all users
        ServerMessage resignNoti = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        resignNoti.setMessage(username + " resigned the game.");
        broadcastMessage(gameID, resignNoti, null);
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
            // keep our auth token as well
            userToAuthToken.put(currentUsername, authToken);



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
        if (members == null) {return;}
        for (String username : members) {
            if (Objects.equals(username, excludeThisUser)) {
                continue;
            }
            // manually get the gameID in case leaving the game did not remove it fast enough
            // race condition??
            Integer actualGameID = userToGame.get(username);
            if (actualGameID == null || actualGameID != gameID) {
                continue; // skip this user if they exist
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
