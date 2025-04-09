package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static ui.EscapeSequences.*;

public class ObserveMode implements ClientMode {
    private final ServerFacade facade;
    private final String username;
    private final String gameID;
    private final String gameName;
    private final String whiteUsername;
    private final String blackUsername;
    private final ChessGame game;
    private final String authToken;

    public ObserveMode(ServerFacade facade, String username, String authToken, String gameID, String gameName,
                       String whiteUsername, String blackUsername, ChessGame game) {
        this.facade = facade;
        WSClientMailman.setActiveMode(this); // update mailman on our current mode
        // upgrade from http to ws via CONNECT now that we are observing a game
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(WSClient.class, URI.create("ws://localhost:8080/ws"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        WSClientMailman.sendConnect(authToken, Integer.parseInt(gameID));

        this.username = username;
        this.authToken = authToken;
        this.gameID = gameID;
        this.gameName = gameName;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.game = game;

        System.out.println("\uD83D\uDC40 Now observing game \"" + gameName + "\". " +
                "Current players: ");
        printGameInfo();
    }

    @Override
    public String help() {
        return String.format("""
        Available commands:
        redraw             - %sredraws the chess board%s
        leave              - %sreturn to the previous menu%s
        help               - %sshow available commands%s
        quit               - %sexit the program%s
        highlight <POS>    - %shighlight legal moves for a piece%s
        """,
                SET_TEXT_UNDERLINE, RESET_TEXT_UNDERLINE,
                SET_TEXT_UNDERLINE, RESET_TEXT_UNDERLINE,
                SET_TEXT_UNDERLINE, RESET_TEXT_UNDERLINE,
                SET_TEXT_UNDERLINE, RESET_TEXT_UNDERLINE,
                SET_TEXT_UNDERLINE, RESET_TEXT_UNDERLINE
        );
    }

    @Override
    public ClientMode eval(String input) {
        var tokens = input.trim().split(" ");
        var cmd = tokens[0].toLowerCase();
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);

        switch (cmd) {
            case "redraw":
                System.out.println("\uD83D\uDC40 Still observing game \"" + gameName + "\". " +
                        "Current players: ");
                printGameInfo();
                new DrawBoard(game, true).draw(null); // Always from white's perspective
                return this;

            case "leave":
                WSClientMailman.sendLeave(authToken, Integer.parseInt(gameID));
                return new PostLoginMode(facade, username, authToken);

            case "quit":
                System.out.println("Goodbye.");
                return null;

            case "highlight":
                return highlight(params);

            case "help":
                System.out.println(help());
                return this;

            default:
                System.out.println("Unknown command: " + cmd);
                System.out.println(help());
                return this;
        }
    }

    private ClientMode highlight(String... params) {
        if (params.length != 1) {
            System.out.println("Usage: highlight <POSITION> (e.g. highlight e2)");
            return this;
        }
        // use the helper, shared for GameMode and ObserveMode
        return HighlightHelper.highlight(params[0], this.game, this, true);
    }

    private void printGameInfo() {
        System.out.println("White: " + getPlayerOrEmpty(whiteUsername));
        System.out.println("Black: " + getPlayerOrEmpty(blackUsername));
    }

    private String getPlayerOrEmpty(String name) {
        return (name == null || name.isBlank()) ? "(empty)" : name;
    }

    public void updateBoard(ChessGame updatedGame) {
        this.game.setBoard(updatedGame.getBoard());
        new DrawBoard(this.game, true).draw(null);
        System.out.println(help());

    }

}
