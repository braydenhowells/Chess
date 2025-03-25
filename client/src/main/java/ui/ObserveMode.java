package ui;

import chess.ChessGame;

import static ui.EscapeSequences.*;

public class ObserveMode implements ClientMode {
    private final ServerFacade facade;
    private final String username;
    private final String gameID;
    private final String gameName;
    private final String whiteUsername;
    private final String blackUsername;
    private final ChessGame game;

    public ObserveMode(ServerFacade facade, String username, String gameID, String gameName,
                       String whiteUsername, String blackUsername, ChessGame game) {
        this.facade = facade;
        this.username = username;
        this.gameID = gameID;
        this.gameName = gameName;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.game = game;

        System.out.println("\uD83D\uDC40 Now observing game \"" + gameName + "\". " +
                "Current players: ");
        printGameInfo();
        new DrawBoard(game, true).draw(); // always draw from white's perspective
        System.out.println(help());
    }

    @Override
    public String help() {
        return String.format("""
            Available commands:
            redraw - %sredraws the chess board%s
            leave  - %sreturn to the previous menu%s
            help   - %sshow available commands%s
            quit   - %sexit the program%s
            """,
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

        switch (cmd) {
            case "redraw":
                System.out.println("\uD83D\uDC40 Still observing game \"" + gameName + "\". " +
                        "Current players: ");
                printGameInfo();
                new DrawBoard(game, true).draw(); // Always from white's perspective
                return this;

            case "leave":
                return new PostLoginMode(facade, username);

            case "quit":
                System.out.println("Goodbye.");
                return null;

            case "help":
                System.out.println(help());
                return this;

            default:
                System.out.println("Unknown command: " + cmd);
                System.out.println(help());
                return this;
        }
    }

    private void printGameInfo() {
        System.out.println("White: " + getPlayerOrEmpty(whiteUsername));
        System.out.println("Black: " + getPlayerOrEmpty(blackUsername));
    }

    private String getPlayerOrEmpty(String name) {
        return (name == null || name.isBlank()) ? "(empty)" : name;
    }
}
