package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor turn;
    private ChessMove LastOpponentMove;
    private Collection<ChessPosition> BlackPositionsOccupied;
    private Collection<ChessPosition> WhitePositionsOccupied;
    private Collection<ChessMove> BlackValidMoves;
    private Collection<ChessMove> WhiteValidMoves;


    public ChessGame() {
        // setup
        ChessBoard gameBoard = new ChessBoard();
        gameBoard.resetBoard();
        this.board = gameBoard;
        this.turn = TeamColor.WHITE;
        this.WhitePositionsOccupied = new ArrayList<>();
        this.BlackPositionsOccupied = new ArrayList<>();
        this.BlackValidMoves = new ArrayList<>();
        this.WhiteValidMoves = new ArrayList<>();

        // add the starting positions into our positions lists
        // royalty white
        for (int i = 1; i < 9; i++) {
            WhitePositionsOccupied.add(new ChessPosition(1, i));
        }
        // pawns white
        for (int i = 1; i < 9; i++) {
            WhitePositionsOccupied.add(new ChessPosition(2, i));
        }
        // royalty black
        for (int i = 1; i < 9; i++) {
            BlackPositionsOccupied.add(new ChessPosition(8, i));
        }
        // pawns black
        for (int i = 1; i < 9; i++) {
            BlackPositionsOccupied.add(new ChessPosition(7, i));
        }
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        if (turn == null) {
            return TeamColor.WHITE;
        }
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        if (this.turn.equals(TeamColor.WHITE)) {
            turn = TeamColor.BLACK;
        }
        else {
            turn = TeamColor.WHITE;
        }
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        return board.getPiece(startPosition).pieceMoves(board, startPosition);
        // no variables needed. just return it exactly

    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();
        ChessPiece piece = this.board.getPiece(startPos);
        ChessPiece.PieceType pieceType = this.board.getPiece(startPos).getPieceType();

        // make sure I am not in check

        // set game turn to other team
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
       throw new RuntimeException("sorry");
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
