package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    private final ChessGame.TeamColor pieceColor;
    private PieceType type; // not final because panws can change

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if (this.type == PieceType.BISHOP) {
            return new BishopMovesCalculator().getMoves(ChessPiece.this, board, myPosition);
        }
        if (this.type == PieceType.ROOK) {
            return new RookMovesCalculator().getMoves(ChessPiece.this, board, myPosition);
        }
        if (this.type == PieceType.QUEEN) {
            Collection<ChessMove> rookMoves = new RookMovesCalculator().getMoves(ChessPiece.this, board, myPosition);
            Collection<ChessMove> bishopMoves = new BishopMovesCalculator().getMoves(ChessPiece.this, board, myPosition);
            Collection<ChessMove> allMoves = new ArrayList<>();
            allMoves.addAll(rookMoves);
            allMoves.addAll(bishopMoves);
            return allMoves;
        }
        if (this.type == PieceType.KING) {
            return new KingMovesCalculator().getMoves(ChessPiece.this, board, myPosition);
        }
        if (this.type == PieceType.KNIGHT) {
            return new KnightMovesCalculator().getMoves(ChessPiece.this, board, myPosition);
        }
        return new PawnMovesCalculator().getMoves(ChessPiece.this, board, myPosition);
    }
}
