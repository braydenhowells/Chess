package chess.piececalculators;

import chess.*;
import chess.movecalculators.UnitMovesCalculator;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator {
    private ChessPiece piece;
    private Collection<ChessMove> moves;
    private ChessBoard board;
    private ChessPosition position;



    public Collection<ChessMove> getMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {
        moves = new ArrayList<>();

        // top right
        moves = UnitMovesCalculator.moves(piece, position, position, board, moves, false, 1, 1);
        // top left
        moves = UnitMovesCalculator.moves(piece, position, position, board, moves, false, 1, -1);
        // bottom right
        moves = UnitMovesCalculator.moves(piece, position, position, board, moves, false, -1, 1);
        // bottom left
        moves = UnitMovesCalculator.moves(piece, position, position, board, moves, false, -1, -1);
        return moves;
    }
}
