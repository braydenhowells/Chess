package chess;

import java.util.Collection;

public class StupidPawnEdgeCasesCalculator {
    private ChessBoard board;
    private Collection<ChessMove> moves;
    private ChessPiece piece;
    private ChessPosition position;

    public Collection<ChessMove> PawnMoves(ChessPiece piece, ChessPosition originalPos, ChessBoard board, Collection<ChessMove> moves, int leftrightValue, int updownValue) {
        // returns just one of the possible moves for a pawn. params are for differences in white and black pawns
        // setup
        int originalRow = originalPos.getRow();
        int originalColumn = originalPos.getColumn();
        System.out.println("Current Position: " + originalPos);
        int targetRow = originalRow + updownValue;
        int targetColumn = originalColumn + leftrightValue;

        // promotional check, if either piece would be moving to the very edge, add the promotion moves
        boolean promote = piece.getTeamColor() == ChessGame.TeamColor.BLACK && targetRow == 1;
        promote = piece.getTeamColor() == ChessGame.TeamColor.WHITE && targetRow == 8;

        // new possible position
        ChessPosition targetEndPosition = new ChessPosition(targetRow, originalColumn);

        // check for an out-of-bounds move. if so, return moves
        if (targetColumn > 8 || targetRow > 8 || targetColumn < 1 || targetRow < 1) {
            return moves;
        }

        // check if a square is occupied
        if (board.getPiece(targetEndPosition) != null) {
            if (leftrightValue != 0) {
                // this is a diagonal move, so we can capture a piece if one exists of the opposite color
                if (!board.getPiece(targetEndPosition).getTeamColor().equals(piece.getTeamColor())) {
                    // this checks if we can promote here
                    if (promote) {
                        moves.add(new ChessMove(originalPos, targetEndPosition, ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(originalPos, targetEndPosition, ChessPiece.PieceType.KNIGHT));
                        moves.add(new ChessMove(originalPos, targetEndPosition, ChessPiece.PieceType.BISHOP));
                        moves.add(new ChessMove(originalPos, targetEndPosition, ChessPiece.PieceType.QUEEN));
                        return moves;
                    } else {
                        moves.add(new ChessMove(originalPos, targetEndPosition, null));
                    }
                }
                // this path means we are blocked by our own teammate
                return moves;
            }
        }

        // passed all checks? make a new move from ORIGINAL position to TARGET position
        if (promote) {
            moves.add(new ChessMove(originalPos, targetEndPosition, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(originalPos, targetEndPosition, ChessPiece.PieceType.KNIGHT));
            moves.add(new ChessMove(originalPos, targetEndPosition, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(originalPos, targetEndPosition, ChessPiece.PieceType.QUEEN));
        } else {
            ChessMove newMove = new ChessMove(originalPos, targetEndPosition, null);
            moves.add(newMove);
            return moves;
        }
        return moves;
    }
}
