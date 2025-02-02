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
        this.board = new ChessBoard();
        this.turn = TeamColor.WHITE;
        this.BlackMoves = new ArrayList<>();
        this.WhiteMoves = new ArrayList<>();
        // get a normal board setup. this can be changed with setBoard for testing
        board.resetBoard();
    }


    public void getTeamMoves() {
        BlackMoves.clear();
        WhiteMoves.clear();
        // get all the moves and positions for either team. this will be used to see if we are in check
        // these are to be updated throughout the game to make looping through all pieces easier than this method

        // check the entire board and populate the move lists
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition boardPosition = new ChessPosition(i, j);
                ChessPiece boardPiece = board.getPiece(boardPosition);
                if (boardPiece != null && boardPiece.getPieceType() != null) {
                    // this is a real piece. see what color it is and add to list
                    if (boardPiece.getTeamColor() == TeamColor.BLACK) {
                        // update the king position if applicable
                        if (boardPiece.getPieceType() == ChessPiece.PieceType.KING) {
                            BlackKingPosition = boardPosition;
                        }
                        BlackMoves.addAll(boardPiece.pieceMoves(board, boardPosition));
                    }
                    if (boardPiece.getTeamColor() == TeamColor.WHITE) {
                        // update the king position if applicable
                        if (boardPiece.getPieceType() == ChessPiece.PieceType.KING) {
                            WhiteKingPosition = boardPosition;
                        }
                        WhiteMoves.addAll(boardPiece.pieceMoves(board, boardPosition));
                    }
                }
            }
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
        if (team == TeamColor.WHITE) {
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
        // setup
        ChessPiece piece = board.getPiece(startPosition);
        Collection<ChessMove> possibleMoves =  piece.pieceMoves(board, startPosition);
        Collection<ChessMove> filteredMoves = new ArrayList<>();
        ChessGame.TeamColor pieceColor = piece.getTeamColor();

        // loop through a moveset for a piece. test each move: would this put us in check??
        for (ChessMove move : possibleMoves) {
            // setup
            boolean isCapture = false;
            ChessPosition endPos = move.getEndPosition();
            ChessPosition startPos = move.getStartPosition();
            ChessPiece capturePiece;
            if (board.getPiece(endPos) != null && !board.getPiece(endPos).getTeamColor().equals(piece.getTeamColor())) {
                isCapture = true;
                capturePiece = new ChessPiece(board.getPiece(endPos).getTeamColor(), board.getPiece(endPos).getPieceType());
            }
            else {
                capturePiece = null;
            }

            // now try making the move by "adding" that piece at the end position
            board.addPiece(endPos, piece);
            System.out.println(board.toString());

            // set the start position of piece currently moving to empty
            board.removePiece(startPos);
            System.out.println(board.toString());
            // update all possible moves to see if enemies can take the king now
            getTeamMoves();

            // if we aren't in check now, then let's add our move to the list
            if (!isInCheck(pieceColor)) {
                filteredMoves.add(move);
            }

            // reset the board to make our next check
            board.addPiece(startPos, piece);
            board.removePiece(endPos);
            if (isCapture) {
                board.addPiece(endPos, capturePiece);
            }
            System.out.println(board.toString());
        }

        // make sure we return this to normal before we leave the function
        getTeamMoves();
        return filteredMoves;
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
        ChessPiece piece = board.getPiece(startPos);
        Collection<ChessMove> availableMoves;

        if (turn == TeamColor.BLACK) {
            availableMoves = BlackMoves;
        }
        else {
            availableMoves = WhiteMoves;
        }

        // if we are not allowed to move here
        if (!availableMoves.contains(move)) {
            throw new InvalidMoveException("Invalid Move!");
        }
        // this path means we have a move that works

        // if king moved, update his position
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            if (turn == TeamColor.BLACK) {
                BlackKingPosition = endPos;
            }
            else {
                WhiteKingPosition = endPos;
            }
        }

        // now we need to update our moves lists
        // loop through current positions of both teams and update available moves

        // set game turn to other team
        setTeamTurn(turn);

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // setup
        Collection<ChessMove> OpponentMoves;
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
            if (endPos.equals(KingPosition)) {
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
        getTeamMoves();
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
