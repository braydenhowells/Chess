package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import requests.JoinRequest;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;
import java.net.URI;
import java.util.Arrays;

import static ui.EscapeSequences.*;

public class GameMode implements ClientMode {
    private final ServerFacade facade;
    private final String username;
    private final String authToken;
    private final String gameID; // this will make phase 6 easier
    private final String playerColor;
    private final String gameName;
    private final ChessGame game;
    private final boolean whitePerspective;

    public GameMode(ServerFacade facade, String username, String authToken, String gameID, String gameName, String playerColor, ChessGame game) {
        this.facade = facade;
        WSClientMailman.setActiveMode(this); // tell mailman what mode we are using
        this.username = username;
        this.authToken = authToken;
        this.gameID = gameID;
        this.playerColor = playerColor.toUpperCase();
        this.whitePerspective = playerColor.equalsIgnoreCase("WHITE");
        this.gameName = gameName;
        this.game = game;

        // upgrade from http to ws via CONNECT now that we are in a game
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(WSClient.class, URI.create("ws://localhost:8080/ws"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        WSClientMailman.sendConnect(authToken, Integer.parseInt(gameID), playerColor);


        // draw board on startup
        DrawBoard picasso = new DrawBoard(this.game, whitePerspective);
        picasso.draw(null);

        System.out.println("\uD83C\uDFC1 " + username + ", you have joined game \"" + gameName + "\" as " + this.playerColor + ".");
        System.out.println(help());
    }

    @Override
    public String help() {
        return String.format("""
            Available commands:
            help               - %sdisplays what actions you can take%s
            redraw             - %sredraws the chess board on your screen%s
            leave              - %sreturns you to the previous menu%s
            move <FROM> <TO>   - %sinput a move to play%s
            resign             - %sforfeit the game and end it%s
            highlight <POS>    - %shighlight legal moves for a piece%s
            """,
                SET_TEXT_UNDERLINE, RESET_TEXT_UNDERLINE,
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
            case "help":
                System.out.println(help());
                return this;

            case "redraw":
                DrawBoard picasso = new DrawBoard(game, whitePerspective); // <- white or black bool
                picasso.draw(null);
                return this;

            case "move":
                return makeMove(params);

            case "leave":
                facade.join(new JoinRequest(playerColor, gameID)); // fake join that is actually leave
                WSClientMailman.sendLeave(authToken, Integer.parseInt(gameID)); // disconnect
                return new PostLoginMode(this.facade, this.username, this.authToken); // return

            case "quit":
                System.out.println("Exiting the game. Goodbye!");
                return null;

            case "highlight":
                return highlight(params);

            default:
                System.out.println("Unknown in-game command: " + cmd);
                System.out.println("Type 'help' to see available commands.");
                return this;
        }
    }


    private ClientMode highlight(String... params) {
        if (params.length != 1) {
            System.out.println("Usage: highlight <POSITION> (e.g. highlight e2)");
            return this;
        }
        // use the helper, shared for GameMode and ObserveMode
        return HighlightHelper.highlight(params[0], this.game, this, whitePerspective);
    }

    public void updateBoard(ChessGame updatedGame) {
        this.game.setBoard(updatedGame.getBoard());
        System.out.println("LOAD_GAME received");
        new DrawBoard(this.game, whitePerspective).draw(null);
    }

    private ClientMode makeMove(String... params) {
        if (params.length != 2) {
            System.out.println("Usage: move <FROM> <TO> (e.g. move e2 e4)");
            return this;
        }

        ChessPosition from = HighlightHelper.formatToPosition(params[0]);
        ChessPosition to = HighlightHelper.formatToPosition(params[1]);

        if (from == null || to == null) {
            System.out.println("Invalid move syntax.");
            System.out.println("Usage: move <FROM> <TO> (e.g. move e2 e4)");
            return this;
        }

        ChessMove move = new ChessMove(from, to, null);
        // TODO: allow promotions for pawns
        WSClientMailman.sendMakeMove(authToken, Integer.parseInt(gameID), move);

        return this;
    }


}
