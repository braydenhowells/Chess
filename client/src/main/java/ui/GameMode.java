package ui;

import chess.ChessGame;
import requests.JoinRequest;

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
        this.username = username;
        this.authToken = authToken;
        this.gameID = gameID;
        this.playerColor = playerColor.toUpperCase();
        this.whitePerspective = playerColor.equalsIgnoreCase("WHITE");
        this.gameName = gameName;
        this.game = game;

        // upgrade from http to ws via CONNECT now that we are in a game
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
                DrawBoard picasso = new DrawBoard(game, whitePerspective);
                // checks if player color is WHITE. if so, then sets whitePerspective as true
                picasso.draw(null);
                return this;

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
}
