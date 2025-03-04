package chess.piececalculators;

import chess.*;
import chess.movecalculators.UnitMovesCalculator;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator {
    private ChessPiece piece;
    private Collection<ChessMove> moves;
    private ChessBoard board;
    private ChessPosition position;



    public Collection<ChessMove> getMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {
        moves = new ArrayList<>();
        // up
        moves = UnitMovesCalculator.moves(piece, position, position, board, moves, false, 1, 0);
        // left
        moves = UnitMovesCalculator.moves(piece, position, position, board, moves, false, 0, -1);
        // right
        moves = UnitMovesCalculator.moves(piece, position, position, board, moves, false, 0, 1);
        // down
        moves = UnitMovesCalculator.moves(piece, position, position, board, moves, false, -1, 0);
        return moves;
    }
}
