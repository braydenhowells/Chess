package chess.move_calculators;

import chess.*;

import java.util.Collection;

public class SpecialMovesCalculator {
    // not recursive, just returns a single move we give it for the pawn. static so we control with param units
    public static Collection<ChessMove> PawnMoves(ChessPiece piece,
                                                  ChessPosition originalPos,
                                                  ChessBoard board,
                                                  Collection<ChessMove> moves,
                                                  int leftrightValue,
                                                  int updownValue) {
        // returns just one of the possible moves for a pawn. params are for differences in white and black pawns
        // setup
        int originalRow = originalPos.getRow();
        int originalColumn = originalPos.getColumn();

        // make a target position to see if we can go there
        int targetRow = originalRow + updownValue;
        int targetColumn = originalColumn + leftrightValue;
        ChessPosition targetEndPosition = new ChessPosition(targetRow, targetColumn);

        // promotional check, if either piece would be moving to the very edge, add the promotion moves
        boolean promote = false;
        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK && targetRow == 1) {
            promote = true;
        }
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE && targetRow == 8) {
            promote = true;
        }

        // check for an out-of-bounds move. if so, return moves
        if (targetColumn > 8 || targetRow > 8 || targetColumn < 1 || targetRow < 1) {
            return moves;
        }

        // check if a square is occupied on forward move
        if (board.getPiece(targetEndPosition) != null && leftrightValue == 0) {
            // this is a 2 or 1 unit forward movement. if we are blocked by either team, not a possible move
            return moves;
        }

        // white pawn 2 space movement: moving forward must be entirely cleared
        if (updownValue == -2) {
            ChessPosition dummyPosition = new ChessPosition(targetRow + 1, targetColumn);
            if (board.getPiece(dummyPosition) != null) {
                // the pawn cannot hop, so moving 2 spaces means that both spaces must be clear
                return moves;
            }
        }

        // black pawn 2 space movement: moving forward must be entirely cleared
        if (updownValue == 2) {
            ChessPosition dummyPosition = new ChessPosition(targetRow - 1, targetColumn);
            if (board.getPiece(dummyPosition) != null) {
                // the pawn cannot hop, so moving 2 spaces means that both spaces must be clear
                return moves;
            }
        }

        // check capture conditions: moving diagonally, enemy is there
        if (leftrightValue != 0) {
            if (board.getPiece(targetEndPosition) != null && board.getPiece(targetEndPosition).getTeamColor() != piece.getTeamColor()) {
                if (promote) {
                    moves = addPromotionMoves(moves, originalPos, targetEndPosition);
                    return moves;
                }
                else {
                    moves.add(new ChessMove(originalPos, targetEndPosition, null));
                    return moves;
                }
            }
            // this path means there is nobody for us to capture in our diagonal quest
            return moves;
        }

        // passed all checks? make a new move from ORIGINAL position to TARGET position
        if (promote) {
            moves = addPromotionMoves(moves, originalPos, targetEndPosition);
            return moves;
        } else {
            moves.add(new ChessMove(originalPos, targetEndPosition, null));
            return moves;
        }
    }

    // func to add all possible promo moves for a pawn
    private static Collection<ChessMove> addPromotionMoves(Collection<ChessMove> moves, ChessPosition originalPos, ChessPosition targetEndPosition) {
        moves.add(new ChessMove(originalPos, targetEndPosition, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(originalPos, targetEndPosition, ChessPiece.PieceType.KNIGHT));
        moves.add(new ChessMove(originalPos, targetEndPosition, ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(originalPos, targetEndPosition, ChessPiece.PieceType.QUEEN));
        return moves;
    }
}


