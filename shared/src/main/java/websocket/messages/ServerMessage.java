package websocket.messages;

import model.GameData;

import java.util.Objects;

/**
 * Represents a message sent from the server to the client over a WebSocket connection.
 * Message type is specified by ServerMessageType. Additional content is included based on the type.
 *
 * - LOAD_GAME: includes the full GameData object.
 * - ERROR: includes an error message.
 * - NOTIFICATION: includes a simple message for the client.
 */

public class ServerMessage {

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    private ServerMessageType serverMessageType;
    private String message;
    private GameData game;

    public ServerMessage(ServerMessageType type) {
        this.serverMessageType = type;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public GameData getGame() {
        return game;
    }

    public void setGame(GameData game) {
        this.game = game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServerMessage)) return false;
        ServerMessage that = (ServerMessage) o;
        return getServerMessageType() == that.getServerMessageType() &&
                Objects.equals(getMessage(), that.getMessage()) &&
                Objects.equals(getGame(), that.getGame());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType(), getMessage(), getGame());
    }
}
