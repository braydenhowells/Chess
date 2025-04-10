package websocket.commands;

import chess.ChessMove;

import java.util.Objects;

/**
 * Represents a command sent from the client to the server over a WebSocket connection.
 * Contains the command type, auth token of the sender, and the ID of the game being referenced.
 *
 * This class is deserialized from JSON. Fields must not be final so they can be populated by Gson.
 */

public class UserGameCommand {

    private String authToken;
    private Integer gameID;
    private CommandType commandType;
    private ChessMove move;

    public UserGameCommand(CommandType commandType, String authToken, Integer gameID, ChessMove move) {
        this.commandType = commandType;
        this.authToken = authToken;
        this.gameID = gameID;
        this.move = move;
    }

    public enum CommandType {
        CONNECT,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public String getAuthToken() {
        return authToken;
    }

    public Integer getGameID() {
        return gameID;
    }

    public ChessMove getMove() {
        return move;
    }

    public void setMove(ChessMove move) {
        this.move = move;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (!(o instanceof UserGameCommand)) {return false;}
        UserGameCommand that = (UserGameCommand) o;
        return getCommandType() == that.getCommandType() &&
                Objects.equals(getAuthToken(), that.getAuthToken()) &&
                Objects.equals(getGameID(), that.getGameID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandType(), getAuthToken(), getGameID());
    }
}
