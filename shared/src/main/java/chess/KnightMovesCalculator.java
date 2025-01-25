package chess;

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
        moves = MovesCalculator.Moves(piece, position, position, board, moves, true, 2, 1);
        moves = MovesCalculator.Moves(piece, position, position, board, moves, true, 1, 2);
        // top left
        moves = MovesCalculator.Moves(piece, position, position, board, moves, true, 2, -1);
        moves = MovesCalculator.Moves(piece, position, position, board, moves, true, 1, -2);
        // bottom right
        moves = MovesCalculator.Moves(piece, position, position, board, moves, true, -2, 1);
        moves = MovesCalculator.Moves(piece, position, position, board, moves, true, -1, 2);
        // bottom left
        moves = MovesCalculator.Moves(piece, position, position, board, moves, true, -2, -1);
        moves = MovesCalculator.Moves(piece, position, position, board, moves, true, -1, -2);
        return moves;
    }
}
