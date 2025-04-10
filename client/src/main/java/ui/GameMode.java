package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import requests.JoinRequest;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;
import java.net.URI;
import java.util.Arrays;
import java.util.Scanner;

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
        WSClientMailman.sendConnect(authToken, Integer.parseInt(gameID));
        // welcome message
        System.out.println("\uD83C\uDFC1 " + username + ", you have joined game \"" + gameName + "\" as " + this.playerColor + ".");
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

            case "resign":
                String confirm = resignPrompt();
                if (confirm == null) {
                    System.out.println("Failed to resign, invalid input.");
                    return this;
                }

                if (confirm.equals("Y")) {
                    WSClientMailman.sendResign(authToken, Integer.parseInt(gameID));
                    System.out.println("You have resigned. GG soldier.");
                } else {
                    System.out.println("Resign cancelled. Never give up!");
                }
                return this;

            case "leave":
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
        this.game.setTeamTurn(updatedGame.getTeamTurn());
        System.out.println("LOAD_GAME received");
        new DrawBoard(this.game, whitePerspective).draw(null);
        System.out.println(help());

    }

    private ClientMode makeMove(String... params) {
        if (params.length != 2) {
            System.out.println("Usage: move <FROM> <TO> (e.g. move e2 e4)");
            return this;
        }

        ChessPosition startPos = HighlightHelper.formatToPosition(params[0]);
        ChessPosition endPos = HighlightHelper.formatToPosition(params[1]);

        if (startPos == null || endPos == null) {
            System.out.println("Invalid move syntax.");
            System.out.println("Usage: move <FROM> <TO> (e.g. move e2 e4)");
            return this;
        }

        ChessGame.TeamColor currentTurn = game.getTeamTurn();
        if (!currentTurn.name().toUpperCase().equals(playerColor)) {
            System.out.println("⏳ It is not currently your turn to move.");
            return this;
        }

        var piece = game.getBoard().getPiece(startPos);
        if (piece == null) {
            System.out.println("There's no piece at " + params[0] + ".");
            return this;
        }

        // promo tiiiiiiiime
        if (piece != null && piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            if ((playerColor == ChessGame.TeamColor.WHITE.name() && endPos.getRow() == 8) ||
                    (playerColor == ChessGame.TeamColor.BLACK.name() && endPos.getRow() == 1)) {
                // this means we have a pawn promo move. time to ask user for a promo piece
                String promoPieceString = promoPrompt();
                if (promoPieceString == null) {
                    System.out.println("Input not recognized for promotion piece. Cancelling move.");
                    return this;
                }

                ChessPiece.PieceType promoPiece;
                switch (promoPieceString) {
                    case "Q":
                        promoPiece = ChessPiece.PieceType.QUEEN;
                        break;
                    case "R":
                        promoPiece = ChessPiece.PieceType.ROOK;
                        break;
                    case "B":
                        promoPiece = ChessPiece.PieceType.BISHOP;
                        break;
                    case "K":
                        promoPiece = ChessPiece.PieceType.KNIGHT;
                        break;
                    default:
                        promoPiece = null; // should never hit
                        break;
                }
                ChessMove move = new ChessMove(startPos, endPos, promoPiece);
                WSClientMailman.sendMakeMove(authToken, Integer.parseInt(gameID), move);
                return this;


            }
        }

        if (!piece.getTeamColor().name().toUpperCase().equals(playerColor)) {
            System.out.println("You can't move your opponent's piece.");
            return this;
        }

        ChessMove move = new ChessMove(startPos, endPos, null);
        WSClientMailman.sendMakeMove(authToken, Integer.parseInt(gameID), move);
        return this;
    }

    private String promoPrompt() {
        System.out.print("Promote to (Q, R, B, K): ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine().trim().toUpperCase();

        switch (input) {
            case "Q", "R", "B", "K":
                return input;
            default:
                return null;
        }
    }

    private String resignPrompt() {
        System.out.print("Are you sure you want to resign? (Y/N): ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine().trim().toUpperCase();

        switch (input) {
            case "Y", "N":
                return input;
            default:
                return null;
        }
    }




}
