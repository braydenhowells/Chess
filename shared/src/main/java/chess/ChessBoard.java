package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] squares = new ChessPiece[8][8];
    public ChessBoard() {}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

     @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("  a b c d e f g h\n"); // column labels
        for (int i = 0; i < squares.length; i++) {
            sb.append(8 - i).append(" "); // row labels
            for (int j = 0; j < squares[i].length; j++) {
                if (squares[i][j] == null) {
                    sb.append(" ");
                }
                else {
                    sb.append(squares[i][j].getPieceType()).append(" "); // add each piece or empty space
                }
            }
            sb.append(8 - i).append("\n"); // end of row with row label
        }
        sb.append("  a b c d e f g h"); // column labels again at the bottom
        return sb.toString();
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[8 - position.getRow()][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[8 - position.getRow()][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */


    public void resetBoard() {
        squares = new ChessPiece[8][8]; // overwrites our old board, with empty pieces (then we fill em)
        // list for looping
        ChessPiece.PieceType[] pieces = {ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.KING, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.ROOK};

        // bottom row white
        for (int i = 0; i < 8; i++) {
            squares[7][i] = new ChessPiece(ChessGame.TeamColor.WHITE, pieces[i]);
        }
        // pawns white
        for (int i = 0; i < 8; i++) {
            squares[6][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        }
        // top row black
        for (int i = 0; i < 8; i++) {
            squares[0][i] = new ChessPiece(ChessGame.TeamColor.BLACK, pieces[i]);
        }
        // pawns black
        for (int i = 0; i < 8; i++) {
            squares[1][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        }
    }
}
