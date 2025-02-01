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
    private Collection<ChessMove> BlackMoves;
    private Collection<ChessMove> WhiteMoves;
    private ChessPosition WhiteKingPosition;
    private ChessPosition BlackKingPosition;



    public ChessGame() {
        // setup
        ChessBoard gameBoard = new ChessBoard();
        gameBoard.resetBoard();
        this.board = gameBoard;
        this.turn = TeamColor.WHITE;
        this.BlackMoves = new ArrayList<>();
        this.WhiteMoves = new ArrayList<>();
        this.WhiteKingPosition = new ChessPosition(1, 5);
        this.BlackKingPosition = new ChessPosition(8, 5);



        // get all the moves we can make for either team. this will be used to see if we are in check
        // royalty white
        for (int i = 1; i < 9; i++) {
            WhiteMoves.addAll(validMoves(new ChessPosition(1, i)));
        }
        // pawns white
        for (int i = 1; i < 9; i++) {
            WhiteMoves.addAll(validMoves(new ChessPosition(2, i)));
        }

        // royalty white
        for (int i = 1; i < 9; i++) {
            BlackMoves.addAll(validMoves(new ChessPosition(8, i)));
        }
        // pawns white
        for (int i = 1; i < 9; i++) {
            BlackMoves.addAll(validMoves(new ChessPosition(7, i)));
        }
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
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
        // setup
        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();

        // check if the position is null
        if (board.getPiece(startPos) == null) {
            throw new InvalidMoveException("Invalid Move! You do not have a piece at that starting location.");
        }
        // check if this piece is actually ours
        if (board.getPiece(startPos).getTeamColor() != this.getTeamTurn()) {
            throw new InvalidMoveException("Invalid Move! The piece at that starting location is not yours.");
        }






        // see if move entered is a move that is possible
        for (ChessMove move : )

        // make sure I am not in check

        // if king moved, update his position

        // set game turn to other team
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // setup
        Collection<ChessMove> OpponentMoves = new ArrayList<>();
        ChessPosition KingPosition;

        // need to see if current team color is in check, so declare what team are the opponents
        if (teamColor == TeamColor.BLACK) {
            OpponentMoves = WhiteMoves;
            KingPosition = BlackKingPosition;

        }
        else {
            OpponentMoves = BlackMoves;
            KingPosition = WhiteKingPosition;
        }

        // find out if the other team could move to your kings location
        for (ChessMove move : OpponentMoves) {
            ChessPosition endPos = move.getEndPosition();
            if (endPos == KingPosition) {
                return true; // there is an opposing piece that can capture our king!
            }
        }
        // this path means that no pieces can capture our king. we are NOT in check
        return false;
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
