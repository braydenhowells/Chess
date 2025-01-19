package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator {
    private ChessPiece piece;
    private Collection<ChessMove> moves;
    private ChessBoard board;
    private ChessPosition position;



    public Collection<ChessMove> getMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {
        // this will use a special calculator for the variety of moves and promotional options that a pawn has
        StupidPawnEdgeCasesCalculator StupidCalc = new StupidPawnEdgeCasesCalculator();
        moves = new ArrayList<>();

        // white piece moveset
        // one space movement option
        moves = StupidCalc.PawnMoves(piece, position, board, moves, 0, 1);
        // two space movement option (initial)

        // diagonal capture options (opposite piece color)

        // ^^ promotional options for edge rows via capture or one space movement, calculated by target position
        // if we have target on the edge, make the target move with 4 enums to give promotion options
        // ^^ this only happens when a move is completely validated w bounds and blockage

        // ^^ bounds check

        // blocked check (either color) for 2, and 1 space movement

        // pawn will only move forward based on piece type for capture or any movement, from color param






        // black piece moveset
        // one space movement option

        // two space movement option (initial)

        // diagonal capture options (opposite piece color)

        // ^^ promotional options for edge rows via capture or one space movement, calculated by target position
        // if we have target on the edge, make the target move with 4 enums to give promotion options
        // ^^ this only happens when a move is completely validated w bounds and blockage

        // ^^ bounds check

        // blocked check (either color) for 2, and 1 space movement

        // pawn will only move forward based on piece type for capture or any movement, from color param


        return moves;
    }
}
