package chess.movecalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;

public class UnitMovesCalculator {
    // recursive function for this class, static because we just control it with param units
    public static Collection<ChessMove> moves(ChessPiece piece,
                                              ChessPosition originalPos,
                                              ChessPosition currentPos,
                                              ChessBoard board,
                                              Collection<ChessMove> moves,
                                              boolean limit,
                                              int updownValue,
                                              int leftrightValue) { // found out you can split these to see them better


        // recursive function to get all possible moves based off of parameters^
        // takes an original position and incrementally adds moves possible FROM that position TO new positions indicated
        // works for straight, diagonal, and L shaped moves via leftright </> and updown +/- values

        // setup
        int currentRow = currentPos.getRow();
        int currentColumn = currentPos.getColumn();

        // make a target position to see if we can go there. this move is based on what kind of directions we pass in (params)
        int targetRow = currentRow + updownValue;
        int targetColumn = currentColumn + leftrightValue;
        ChessPosition targetEndPosition = new ChessPosition(targetRow, targetColumn);

        // base case: we are trying an out-of-bounds move, signals recursion stop
        if (targetColumn > 8 || targetRow > 8 || targetColumn < 1 || targetRow < 1) {
            return moves;
        }

        //base case: square we want to move to is occupied, by either team
        if (board.getPiece(targetEndPosition) != null) {
            if (!board.getPiece(targetEndPosition).getTeamColor().equals(piece.getTeamColor())) {
                // this path means that another piece was detected, but we can capture it
                // we will add this capture move to our list and then return
                ChessMove targetMove = new ChessMove(originalPos, targetEndPosition, null);
                moves.add(targetMove);
            }
            // this path: our own teammate is on this square, so we just return
            return moves;
            // ^^ stop collecting moves now, regardless of which color piece we ran into
        }

        // passed base cases? make a new move from ORIGINAL position to TARGET position
        ChessMove targetMove = new ChessMove(originalPos, targetEndPosition, null);
        moves.add(targetMove);

        // pieces like the king and the knight do not want recursion, as they can only move 1 unit
        // the king has a unit of 1 move, the knight has a unit of 1 L-shaped move
        // the limit param will stop after calculating just 1 unit of movement, instead of repeatedly chaining those units
        if (limit) {
            return moves;
        }

        // recurse for pieces like rook, bishop, and queen
        // these pieces have no movement limit and can move until blocked or on the edge of the board
        moves(piece, originalPos, targetEndPosition, board, moves, false, updownValue, leftrightValue);
        return moves;
    }
}
