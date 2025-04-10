package ui;

import chess.ChessMove;
import com.google.gson.Gson;
import model.GameData;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;


// handles sending AND receiving websocket messages between client and server
// delivers messages from the server to the ui and sends commands from the client to the server

public class WSClientMailman {

    private static ClientMode currentMode; // for LOAD GAME messages

    public static void setActiveMode(ClientMode mode) {
        currentMode = mode; // tell the mailman to use observe or game mode when sorting messages
    }


    // called automatically when the server sends a message to the client
    public static void handleServerMessage(String msg) {
        ServerMessage m = new Gson().fromJson(msg, ServerMessage.class);

        switch (m.getServerMessageType()) {
            case LOAD_GAME -> {
                if (currentMode instanceof GameMode gameMode) {
                    gameMode.updateBoard(m.getGame().game());
                } else if (currentMode instanceof ObserveMode observeMode) {
                    observeMode.updateBoard(m.getGame().game());
                    GameData data = m.getGame();
                    observeMode.updatePlayers(data.whiteUsername(), data.blackUsername());
                }
            }

            case NOTIFICATION -> {
                // check to see if someone joined/left. if so, update players in observe mode
                if (currentMode instanceof ObserveMode observeMode) {
                    String message = m.getMessage();
                    System.out.println(message);
                    if (message != null && (message.contains("joined as") || message.contains("left"))) {
                        GameData data = m.getGame();
                        if (data != null) {
                            observeMode.updatePlayers(data.whiteUsername(), data.blackUsername());
                        }
                        else {
                            System.out.println("uh oh");
                        }
                    }
                }
                System.out.println("📢 " + m.getMessage()); // default
            }

            case ERROR -> {
                System.err.println("🚫 " + m.getErrorMessage());
            }
        }
    }


    // sends a CONNECT command to the server
    public static void sendConnect(String authToken, int gameID) {
        UserGameCommand command = new UserGameCommand(
                UserGameCommand.CommandType.CONNECT,
                authToken,
                gameID,
                null // no move here, just a connection
        );

        String json = new Gson().toJson(command);
        WSClient.sendRaw(json);
    }

    public static void sendLeave(String authToken, int gameID) {
        UserGameCommand command = new UserGameCommand(
                UserGameCommand.CommandType.LEAVE,
                authToken,
                gameID,
                null // again, no move
        );

        String json = new Gson().toJson(command);
        WSClient.sendRaw(json);
    }

    public static void sendMakeMove(String authToken, int gameID, ChessMove move) {
        UserGameCommand command = new UserGameCommand(
                UserGameCommand.CommandType.MAKE_MOVE,
                authToken,
                gameID,
                move // include the move here
        );

        String json = new Gson().toJson(command);
        WSClient.sendRaw(json);
    }

    public static void sendResign(String authToken, int gameID) {
        UserGameCommand command = new UserGameCommand(
                UserGameCommand.CommandType.RESIGN,
                authToken,
                gameID,
                null // no more moves :( boohoo
        );

        WSClient.sendRaw(new Gson().toJson(command));
    }




}
