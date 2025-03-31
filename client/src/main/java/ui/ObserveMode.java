package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

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
        new DrawBoard(game, true).draw(null); // always draw from white's perspective
        System.out.println(help());
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
                return new PostLoginMode(facade, username);

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

        ChessPosition pos = formatToPosition(params[0]);
        if (pos == null) {
            System.out.println("Invalid position.");
            System.out.println("Usage: highlight <POSITION> (e.g. highlight e2)");

            return this;
        }

        ChessPiece piece = game.getBoard().getPiece(pos);
        if (piece == null) {
            System.out.println("There is no piece at that position.");
            System.out.println("Usage: highlight <POSITION> (e.g. highlight e2)");
            return this;
        }

        Collection<ChessMove> legalMoves = game.validMoves(pos);
        Collection<ChessPosition> highlights = new ArrayList<>();
        highlights.add(pos); // make sure we highlight our own position also

        for (ChessMove move : legalMoves) {
            highlights.add(move.getEndPosition());
        }

        new DrawBoard(game, true).draw(highlights);
        return this;
    }


    private ChessPosition formatToPosition(String str) {
        if (str.length() != 2) return null;

        char file = Character.toLowerCase(str.charAt(0));
        char rankChar = str.charAt(1);

        if (file < 'a' || file > 'h' || rankChar < '1' || rankChar > '8') {
            return null;
        }

        int col = file - 'a' + 1; // this returns it to 1 index
        int row = Character.getNumericValue(rankChar);
        return new ChessPosition(row, col);
    }



    private void printGameInfo() {
        System.out.println("White: " + getPlayerOrEmpty(whiteUsername));
        System.out.println("Black: " + getPlayerOrEmpty(blackUsername));
    }

    private String getPlayerOrEmpty(String name) {
        return (name == null || name.isBlank()) ? "(empty)" : name;
    }
}
