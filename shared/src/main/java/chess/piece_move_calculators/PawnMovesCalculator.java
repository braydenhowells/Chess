package chess.piece_move_calculators;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator {
    private ChessPiece piece;
    private Collection<ChessMove> moves;
    private ChessBoard board;
    private ChessPosition position;



    public Collection<ChessMove> getMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {
        // this will use a special calculator for the variety of moves and promotional options that a pawn has
        moves = new ArrayList<>();

        // white piece moveset
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            // one space movement option
            moves = SpecialMovesCalculator.PawnMoves(piece, position, board, moves, 0, 1);
            // two space movement option (initial)
            if (position.getRow() == 2) {
                moves = SpecialMovesCalculator.PawnMoves(piece, position, board, moves, 0, 2);
            }
            // diagonal capture options (opposite piece color)
            moves = SpecialMovesCalculator.PawnMoves(piece, position, board, moves, -1, 1);
            moves = SpecialMovesCalculator.PawnMoves(piece, position, board, moves, 1, 1);
        }

        // black piece moveset
        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            // one space movement option
            moves = SpecialMovesCalculator.PawnMoves(piece, position, board, moves, 0, -1);
            // two space movement option (initial)
            if (position.getRow() == 7) {
                moves = SpecialMovesCalculator.PawnMoves(piece, position, board, moves, 0, -2);
            }
            // diagonal capture options (opposite piece color)
            moves = SpecialMovesCalculator.PawnMoves(piece, position, board, moves, -1, -1);
            moves = SpecialMovesCalculator.PawnMoves(piece, position, board, moves, 1, -1);
        }
        return moves;
    }
}
