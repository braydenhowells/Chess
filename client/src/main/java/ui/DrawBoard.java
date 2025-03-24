package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import static ui.EscapeSequences.*;

public class DrawBoard {
    private final ChessGame game;
    private final boolean whitePerspective;

    public DrawBoard(ChessGame game, boolean whitePerspective) {
        this.game = game;
        this.whitePerspective = whitePerspective;
    }

    public void draw() {
        ChessBoard board = game.getBoard();

        // ranks = row ----
        // files = column ||||
        // reverse from white or black
        char[] files = new char[]{'a','b','c','d','e','f','g','h'};
        int[] ranks = whitePerspective ? new int[]{8,7,6,5,4,3,2,1} :
                new int[]{1,2,3,4,5,6,7,8};

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {

                // Borders
                if (row == 0 || row == 9) {
                    // File labels (columns)
                    if (col >= 1 && col <= 8) {
                        System.out.print(SET_TEXT_COLOR_LIGHT_GREY + " " + files[col - 1] + " " + RESET_TEXT_COLOR);
                    } else {
                        System.out.print("   ");
                    }
                } else if (col == 0 || col == 9) {
                    // Rank labels (rows)
                    System.out.print(SET_TEXT_COLOR_LIGHT_GREY + " " + ranks[row - 1] + " " + RESET_TEXT_COLOR);
                } else {
                    // Chessboard square
                    int fileIndex = whitePerspective ? col - 1 : 8 - col;
                    int rankIndex = whitePerspective ? 8 - row : row - 1;
                    ChessPiece piece = board.getPiece(new ChessPosition(rankIndex + 1, fileIndex + 1));
                    boolean isLightSquare = (rankIndex + fileIndex) % 2 == 0;

                    String bgColor = isLightSquare ? SET_BG_COLOR_WHITE : SET_BG_COLOR_BROWN;
                    String symbol = getPieceSymbol(piece);

                    System.out.print(bgColor + symbol + RESET_BG_COLOR);
                }
            }
            System.out.println();
        }
    }


    private String getPieceSymbol(ChessPiece piece) {
        // see if there is a piece here
        if (piece == null) return EMPTY;
        return switch (piece.getTeamColor()) {
            case WHITE -> switch (piece.getPieceType()) {
                case KING -> WHITE_KING;
                case QUEEN -> WHITE_QUEEN;
                case ROOK -> WHITE_ROOK;
                case BISHOP -> WHITE_BISHOP;
                case KNIGHT -> WHITE_KNIGHT;
                case PAWN -> WHITE_PAWN;
            };
            case BLACK -> switch (piece.getPieceType()) {
                case KING -> BLACK_KING;
                case QUEEN -> BLACK_QUEEN;
                case ROOK -> BLACK_ROOK;
                case BISHOP -> BLACK_BISHOP;
                case KNIGHT -> BLACK_KNIGHT;
                case PAWN -> BLACK_PAWN;
            };
        };
    }

}
