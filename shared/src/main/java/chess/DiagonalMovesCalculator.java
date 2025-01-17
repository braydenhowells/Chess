package chess;

import java.util.Collection;

public class DiagonalMovesCalculator {
    private ChessBoard board;
    private Collection<ChessMove> moves;
    private ChessPiece piece;
    private ChessPosition position;

    public Collection<ChessMove> diagonalMoves(ChessPiece piece, ChessPosition originalPos, ChessPosition currentPos, ChessBoard board, Collection<ChessMove> moves, boolean limit, int updownValue, int leftrightValue) {
        // see if diagonal move is possible, then recurse
        int currentRow = currentPos.getRow();
        int currentColumn = currentPos.getColumn();
        System.out.println("Current Position: (" + currentRow + ", " + currentColumn + ")");
        System.out.println("updownValue: " + updownValue + ", leftrightValue: " + leftrightValue);

    // all edge base cases, checking if our current position is on the edge of our desired move
    // base case 1
        if (currentRow == 1 && updownValue == -1) {
            // this is the bottom most row
            System.out.println("Base Case 1: Hit the topmost row. Stopping recursion.");
            return moves;
        }
    // base case 2
        if (currentColumn == 8 && leftrightValue == 1) {
            // this is the right most column
            System.out.println("Base Case 2: Hit the rightmost column. Stopping recursion.");
            return moves;
        }
    // base case 3
        if (currentRow == 8 && updownValue == 1) {
            // this is the top most row
            System.out.println("Base Case 3: Hit the bottommost row. Stopping recursion.");
            return moves;
        }
    // base case 4
        if (currentColumn == 1 && leftrightValue == -1) {
            // this is the left most column
            System.out.println("Base Case 4: Hit the leftmost column. Stopping recursion.");
            return moves;
        }


        // make a target move to see if we can go there. this move is based on what kind of diagonal direction we pass in
        ChessPosition targetEndPosition = new ChessPosition(  currentRow + updownValue, currentColumn + leftrightValue);

        //not a base case necessarily, but check if anyone is on the target square
        if (board.getPiece(targetEndPosition) != null) {
            // this checks if the space is occupied at all. if it is not, we keep going
            if (!board.getPiece(targetEndPosition).getTeamColor().equals(piece.getTeamColor())) {
                // this path means that another piece was detected, but we can capture it. we will add this move to our list and then return
                ChessMove targetMove = new ChessMove(currentPos, targetEndPosition, null);
                moves.add(targetMove);
            }
            // this path means the two pieces are the same color. that means we can't move there, so we return
            return moves; // stop collecting moves after we find a piece, regardless of its color
        }
        // action of recursion now that we haven't hit the base cases: add target move to collection
        ChessMove targetMove = new ChessMove(originalPos, targetEndPosition, null);
        moves.add(targetMove);
        // if there is a limit, we should only get the diagonal for 1 space away. we already did that ^ so we return
        if (limit) {
            return moves;
        }
        // recurse
        diagonalMoves(piece, originalPos, targetEndPosition, board, moves, false, updownValue, leftrightValue);
        return moves;
    }



}
