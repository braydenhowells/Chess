package chess.piececalculators;

import chess.*;
import chess.movecalculators.UnitMovesCalculator;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator {
    private ChessPiece piece;
    private Collection<ChessMove> moves;
    private ChessBoard board;
    private ChessPosition position;



    public Collection<ChessMove> getMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {
        moves = new ArrayList<>();

        // top right
        moves = UnitMovesCalculator.moves(piece, position, position, board, moves, true, 2, 1);
        moves = UnitMovesCalculator.moves(piece, position, position, board, moves, true, 1, 2);
        // top left
        moves = UnitMovesCalculator.moves(piece, position, position, board, moves, true, 2, -1);
        moves = UnitMovesCalculator.moves(piece, position, position, board, moves, true, 1, -2);
        // bottom right
        moves = UnitMovesCalculator.moves(piece, position, position, board, moves, true, -2, 1);
        moves = UnitMovesCalculator.moves(piece, position, position, board, moves, true, -1, 2);
        // bottom left
        moves = UnitMovesCalculator.moves(piece, position, position, board, moves, true, -2, -1);
        moves = UnitMovesCalculator.moves(piece, position, position, board, moves, true, -1, -2);
        return moves;
    }
}
