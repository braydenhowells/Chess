package ui;

import com.google.gson.Gson;
import websocket.commands.UserGameCommand;


// handles sending AND receiving websocket messages between client and server
// delivers messages from the server to the ui and sends commands from the client to the server

public class WSClientMailman {

    // called automatically when the server sends a message to the client
    public static void handleServerMessage(String msg) {
        // for now, we just print the message
        System.out.println("mailman delivered: " + msg);
    }

    // sends a CONNECT command to the server
    public static void sendConnect(String authToken, int gameID, String color) {
        UserGameCommand command = new UserGameCommand(
                UserGameCommand.CommandType.CONNECT,
                authToken,
                gameID,
                color // will be "WHITE", "BLACK", or "OBSERVER"
        );

        String json = new Gson().toJson(command);
        WSClient.sendRaw(json);
    }
}
