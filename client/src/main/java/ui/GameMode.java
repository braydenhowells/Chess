package ui;

import static ui.EscapeSequences.*;

public class GameMode implements ClientMode {
    private final ServerFacade facade;
    private final String username;
    private final String gameID; // this will make phase 6 easier
    private final String playerColor;
    private final String gameName;

    public GameMode(ServerFacade facade, String username, String gameID, String gameName, String playerColor) {
        this.facade = facade;
        this.username = username;
        this.gameID = gameID;
        this.playerColor = playerColor.toUpperCase();
        this.gameName = gameName;
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
        var tokens = input.split(" ");
        var cmd = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";

        switch (cmd) {
            case "help":
                System.out.println(help());
                return this;
            case "redraw":
                System.out.println("Redrawing the board... (dakine)");
                return this;
            case "leave":
                return new PostLoginMode(this.facade, this.username);
            case "quit":
                System.out.println("Exiting the game. Goodbye!");
                return null;
            default:
                System.out.println("Unknown in-game command: " + cmd);
                System.out.println("Type 'help' to see available commands.");
                return this;
        }
    }





}