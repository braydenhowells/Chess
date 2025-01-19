package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator {
    private ChessPiece piece;
    private Collection<ChessMove> moves;
    private ChessBoard board;
    private ChessPosition position;



    public Collection<ChessMove> getMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {

        MovesCalculator Calc = new MovesCalculator();
        moves = new ArrayList<>();
        // up
        moves = Calc.Moves(piece, position, position, board, moves, false, 1, 0);
        // left
        moves = Calc.Moves(piece, position, position, board, moves, false, 0, -1);
        // right
        moves = Calc.Moves(piece, position, position, board, moves, false, 0, 1);
        // down
        moves = Calc.Moves(piece, position, position, board, moves, false, -1, 0);
        return moves;
    }
}
