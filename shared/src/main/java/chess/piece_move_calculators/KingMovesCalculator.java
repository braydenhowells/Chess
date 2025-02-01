package chess.piece_move_calculators;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator {
    private ChessPiece piece;
    private Collection<ChessMove> moves;
    private ChessBoard board;
    private ChessPosition position;



    public Collection<ChessMove> getMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {
        moves = new ArrayList<>();

        // top right
        moves = UnitMovesCalculator.Moves(piece, position, position, board, moves, true, 1, 1);
        // top left
        moves = UnitMovesCalculator.Moves(piece, position, position, board, moves, true, 1, -1);
        // bottom right
        moves = UnitMovesCalculator.Moves(piece, position, position, board, moves, true, -1, 1);
        // bottom left
        moves = UnitMovesCalculator.Moves(piece, position, position, board, moves, true, -1, -1);
        // up
        moves = UnitMovesCalculator.Moves(piece, position, position, board, moves, true, 1, 0);
        // left
        moves = UnitMovesCalculator.Moves(piece, position, position, board, moves, true, 0, -1);
        // right
        moves = UnitMovesCalculator.Moves(piece, position, position, board, moves, true, 0, 1);
        // down
        moves = UnitMovesCalculator.Moves(piece, position, position, board, moves, true, -1, 0);
        return moves;
    }
}
