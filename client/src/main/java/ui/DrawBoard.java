package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;

import static ui.EscapeSequences.*;

public class DrawBoard {
    private final ChessGame game;
    private final boolean whitePerspective;

    public DrawBoard(ChessGame game, boolean whitePerspective) {
        this.game = game;
        this.whitePerspective = whitePerspective;
    }

    public void draw(Collection<ChessPosition> highlights) {
        System.out.println(); // skip a line before printing
        // setup board and labels
        ChessBoard board = game.getBoard();
        // below we will draw NOT according to the counter (row or col) in the loops,
        // instead we will use RANKS and FILES to dictate the contents of a square
        // this makes switching between white and black views easier because:
        // the files are the same, but the ranks are flipped
        // keeping track of those makes the indexing much cleaner
        char[] files = whitePerspective? new char[]{'a','b','c','d','e','f','g','h'} : // files = columns |||
                new char[]{'h','g','f','e','d','c','b','a'};
        int[] ranks = whitePerspective ? new int[]{8,7,6,5,4,3,2,1} : // ranks = rows ----
                new int[]{1,2,3,4,5,6,7,8}; // reverse for black

        // loop over 10 x 10 grid
        for (int row = 0; row < 10; row++) {
            if (row == 0 || row == 9) {
                System.out.print("\u2009"); // 2009 is a thin space, aligns SO clean
                // add thin space to front of first and last row
            }

            for (int col = 0; col < 10; col++) {
                //
                if (row == 0 || row == 9) {
                    // letters on border
                    if (col >= 1 && col <= 8) {
                        String label = String.format(" %s\u2003", files[col - 1]);
                        System.out.print(SET_TEXT_COLOR_LIGHT_GREY + label + RESET_TEXT_COLOR);
                    } else {
                        System.out.print("   ");
                    }
                } else if (col == 0 || col == 9) {
                    // numbers on the border
                    System.out.print(SET_TEXT_COLOR_LIGHT_GREY + " " + ranks[row - 1] + " " + RESET_TEXT_COLOR);
                } else {
                    // inner chess board area

                    // a few items for setting up the print:
                    char fileChar = files[col - 1]; // grab the current letter from files list
                    int fileIndex = fileChar - 'a'; // zero index the files by subtracting 97 (a) in ascii
                    // this makes a = 0, b = 1, c = 2, and so forth
                    // we will add 1 to this zero index when it is needed
                    int rankIndex = ranks[row - 1]; // 1 index the ranks
                    // if we are in row 2 of grid (aka top row of board) ^^ this will get rank 1

                    ChessPosition currentPos = new ChessPosition(rankIndex, fileIndex + 1);
                    // used for highlighting

                    // find out if we have a piece here
                    ChessPiece piece = board.getPiece(new ChessPosition(rankIndex, fileIndex + 1));
                    // get the background off the checkerboard patter
                    boolean isLightSquare = (rankIndex + fileIndex) % 2 == 0;
                    // this adds A + 1 = A1 which has an odd value, means it is dark
                    // the next file is B1, which adds to be even, meaning it is light

                    // determine background: regular or highlight
                    String bgColor;
                    if (highlights != null && highlights.contains(currentPos)) {
                        bgColor = isLightSquare ? SET_BG_COLOR_YELLOW : SET_BG_COLOR_HIGHLIGHT_DARK;
                    } else {
                        bgColor = isLightSquare ? SET_BG_COLOR_TAN : SET_BG_COLOR_GREEN_CUSTOM;
                    }



                    // set up the different colors and pieces, as applicable
                    String symbol = getPieceSymbol(piece);
                    String textColor = getPieceColor(piece);

                    System.out.print(bgColor + textColor + symbol + RESET_TEXT_COLOR + RESET_BG_COLOR);
                }
            }
            System.out.println();
        }
    }


    private String getPieceSymbol(ChessPiece piece) {
        if (piece == null) {return EMPTY;}

        return switch (piece.getPieceType()) {
            case KING -> BLACK_KING;
            case QUEEN -> BLACK_QUEEN;
            case ROOK -> BLACK_ROOK;
            case BISHOP -> BLACK_BISHOP;
            case KNIGHT -> BLACK_KNIGHT;
            case PAWN -> BLACK_PAWN;
        };
    }


    private String getPieceColor(ChessPiece piece) {
        if (piece == null) {return "";} // No color for empty square
        return (piece.getTeamColor() == ChessGame.TeamColor.BLACK)
                ? SET_TEXT_COLOR_BLACK
                : SET_TEXT_COLOR_WHITE;
    }


}
