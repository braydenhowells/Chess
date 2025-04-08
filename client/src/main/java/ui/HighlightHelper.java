package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class HighlightHelper {
    public static ClientMode highlight(String userInputPos, ChessGame game, ClientMode mode, boolean whitePerspective) {

        ChessPosition pos = formatToPosition(userInputPos);
        if (pos == null) {
            System.out.println("Invalid position.");
            System.out.println("Usage: highlight <POSITION> (e.g. highlight e2)");

            return mode;
        }

        ChessPiece piece = game.getBoard().getPiece(pos);
        if (piece == null) {
            System.out.println("There is no piece at that position.");
            System.out.println("Usage: highlight <POSITION> (e.g. highlight e2)");
            return mode;
        }

        Collection<ChessMove> legalMoves = game.validMoves(pos);
        Collection<ChessPosition> highlights = new ArrayList<>();
        highlights.add(pos); // make sure we highlight our own position also

        for (ChessMove move : legalMoves) {
            highlights.add(move.getEndPosition());
        }

        new DrawBoard(game, whitePerspective).draw(highlights);
        return mode;
    }

    public static ChessPosition formatToPosition(String userInputPos) {
        if (userInputPos.length() != 2) return null;

        char file = Character.toLowerCase(userInputPos.charAt(0));
        char rankChar = userInputPos.charAt(1);

        if (file < 'a' || file > 'h' || rankChar < '1' || rankChar > '8') {
            return null;
        }

        int col = file - 'a' + 1; // this returns it to 1 index
        int row = Character.getNumericValue(rankChar);
        return new ChessPosition(row, col);
    }
}
