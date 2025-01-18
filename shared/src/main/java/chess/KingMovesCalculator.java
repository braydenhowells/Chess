package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator {
    private ChessPiece piece;
    private Collection<ChessMove> moves;
    private ChessBoard board;
    private ChessPosition position;



    public Collection<ChessMove> getMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {

        MovesCalculator Calc = new MovesCalculator();
        moves = new ArrayList<>();

        // top right
        moves = Calc.diagonalMoves(piece, position, position, board, moves, true, 1, 1);
        // top left
        moves = Calc.diagonalMoves(piece, position, position, board, moves, true, 1, -1);
        // bottom right
        moves = Calc.diagonalMoves(piece, position, position, board, moves, true, -1, 1);
        // bottom left
        moves = Calc.diagonalMoves(piece, position, position, board, moves, true, -1, -1);
        // up
        moves = Calc.diagonalMoves(piece, position, position, board, moves, true, 1, 0);
        // left
        moves = Calc.diagonalMoves(piece, position, position, board, moves, true, 0, -1);
        // right
        moves = Calc.diagonalMoves(piece, position, position, board, moves, true, 0, 1);
        // down
        moves = Calc.diagonalMoves(piece, position, position, board, moves, true, -1, 0);
        return moves;
    }
}
