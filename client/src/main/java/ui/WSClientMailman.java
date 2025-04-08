package ui;

import com.google.gson.Gson;
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
                }
            }

            case NOTIFICATION -> {
                System.out.println("📢 " + m.getMessage());
            }

            case ERROR -> {
                System.err.println("🚫 " + m.getErrorMessage());
            }
        }
    }


    // sends a CONNECT command to the server
    public static void sendConnect(String authToken, int gameID, String color) {
        UserGameCommand command = new UserGameCommand(
                UserGameCommand.CommandType.CONNECT,
                authToken,
                gameID
        );

        String json = new Gson().toJson(command);
        WSClient.sendRaw(json);
    }

    public static void sendLeave(String authToken, int gameID) {
        UserGameCommand command = new UserGameCommand(
                UserGameCommand.CommandType.LEAVE,
                authToken,
                gameID);

        String json = new Gson().toJson(command);
        WSClient.sendRaw(json);
    }

}
