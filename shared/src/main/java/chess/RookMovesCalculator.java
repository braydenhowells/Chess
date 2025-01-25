package chess;

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
        moves = MovesCalculator.Moves(piece, position, position, board, moves, false, 1, 0);
        // left
        moves = MovesCalculator.Moves(piece, position, position, board, moves, false, 0, -1);
        // right
        moves = MovesCalculator.Moves(piece, position, position, board, moves, false, 0, 1);
        // down
        moves = MovesCalculator.Moves(piece, position, position, board, moves, false, -1, 0);
        return moves;
    }
}
