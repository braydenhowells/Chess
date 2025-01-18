package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator {
    private ChessPiece piece;
    private Collection<ChessMove> moves;
    private ChessBoard board;
    private ChessPosition position;



    public Collection<ChessMove> getMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {

        MovesCalculator Calc = new MovesCalculator();
        moves = new ArrayList<>();

        // top right
        moves = Calc.diagonalMoves(piece, position, position, board, moves, false, 1, 1);
        // top left
        moves = Calc.diagonalMoves(piece, position, position, board, moves, false, 1, -1);
        // bottom right
        moves = Calc.diagonalMoves(piece, position, position, board, moves, false, -1, 1);
        // bottom left
        moves = Calc.diagonalMoves(piece, position, position, board, moves, false, -1, -1);
        // convert to string here?
        return moves;
    }
}
