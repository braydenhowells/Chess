package chess;

import java.util.ArrayList;
import java.util.Collection;

public class ChessGame {
    private ChessBoard board;
    private TeamColor turn;
    private ChessMove lastOpponentMove;
    private Collection<ChessPosition> whitePositions;
    private Collection<ChessPosition> blackPositions;
    private Collection<ChessMove> blackMoves;
    private Collection<ChessMove> whiteMoves;
    private ChessPosition whiteKingPosition;
    private ChessPosition blackKingPosition;


    public ChessGame() {
        // setup
        this.board = new ChessBoard();
        this.turn = TeamColor.WHITE;
        this.blackPositions = new ArrayList<>();
        this.whitePositions = new ArrayList<>();
        this.blackMoves = new ArrayList<>();
        this.whiteMoves = new ArrayList<>();
        // get a normal board setup. this can be changed with setBoard for testing
        board.resetBoard();
    }


    public void getTeamMoves() {
        blackMoves.clear();
        whiteMoves.clear();
        blackPositions.clear();
        whitePositions.clear();

        // get all the moves and positions for either team. this will be used to see if we are in check
        // these are to be updated throughout the game to make looping through all pieces easier than this method

        // check the entire board and populate the move lists
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition boardPosition = new ChessPosition(i, j);
                ChessPiece boardPiece = board.getPiece(boardPosition);

                // if the piece is not there or is somehow null, we skip this iteration of the for loop
                if (boardPiece == null || boardPiece.getPieceType() == null) {
                    continue;
                }

                // this is a real piece. see what color it is and add to list
                if (boardPiece.getTeamColor() == TeamColor.BLACK) {
                    // update the king position if applicable
                    if (boardPiece.getPieceType() == ChessPiece.PieceType.KING) {
                        blackKingPosition = boardPosition;
                    }
                    // update all the moves for this piece
                    blackMoves.addAll(boardPiece.pieceMoves(board, boardPosition));
                    // add this position to our board
                    blackPositions.add(boardPosition);
                }
                if (boardPiece.getTeamColor() == TeamColor.WHITE) {
                    // update the king position if applicable
                    if (boardPiece.getPieceType() == ChessPiece.PieceType.KING) {
                        whiteKingPosition = boardPosition;
                    }
                    // update all the moves for this piece
                    whiteMoves.addAll(boardPiece.pieceMoves(board, boardPosition));
                    // add this position to our board
                    whitePositions.add(boardPosition);
                }

            }
        }
    }

    public TeamColor getTeamTurn() {
        return this.turn;
    }

    public void setTeamTurn(TeamColor team) {
        this.turn = team;
    }

    public enum TeamColor {
        WHITE,
        BLACK
    }

    public void checkEnPassant(ChessMove lastOpMove, Collection<ChessMove> possibleMoves, ChessGame.TeamColor pColor, ChessPosition startPos) {
        if (Math.abs(lastOpMove.getEndPosition().getRow() - lastOpMove.getStartPosition().getRow()) == 2) {
            // this means that the last pawn move was a 2 row move
            if (lastOpMove.getEndPosition().getRow() == startPos.getRow()) {
                // this means that the pawn just moved to our row. we are golden
                if (pColor == TeamColor.BLACK) {
                    ChessPosition bPos = new ChessPosition(lastOpMove.getEndPosition().getRow() - 1, lastOpMove.getEndPosition().getColumn());
                    possibleMoves.add(new ChessMove(startPos, bPos, null));
                }
                ChessPosition wPos = new ChessPosition(lastOpMove.getEndPosition().getRow() + 1, lastOpMove.getEndPosition().getColumn());
                possibleMoves.add(new ChessMove(startPos, wPos, null));
            }
        }
    }

    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        // setup
        ChessPiece piece = board.getPiece(startPosition);
        Collection<ChessMove> possibleMoves =  piece.pieceMoves(board, startPosition);
        Collection<ChessMove> filteredMoves = new ArrayList<>();
        ChessGame.TeamColor pieceColor = piece.getTeamColor();

        // en passant
        if (lastOpponentMove != null) {
            if ((board.getPiece(lastOpponentMove.getEndPosition()).getPieceType() == ChessPiece.PieceType.PAWN) &&
                    piece.getPieceType() == ChessPiece.PieceType.PAWN) {
                // this means that the last move was a pawn and the current piece we are looking at is a pawn
                checkEnPassant(lastOpponentMove, possibleMoves, pieceColor, startPosition);
            }
        }

        // castle?? maybe someday sigh

        // loop through a moveset for a piece. test each move: would this put us in check??
        for (ChessMove move : possibleMoves) {
            // setup
            boolean isCapture = false;
            ChessPosition endPos = move.getEndPosition();
            ChessPosition startPos = move.getStartPosition();
            ChessPiece capturePiece;
            // if we are capturing a piece during our theoretical move, we need to record what it was to put it back afterward
            if (board.getPiece(endPos) != null && !board.getPiece(endPos).getTeamColor().equals(piece.getTeamColor())) {
                isCapture = true;
                capturePiece = new ChessPiece(board.getPiece(endPos).getTeamColor(), board.getPiece(endPos).getPieceType());
            }
            else {
                capturePiece = null;
            }

            // now try making the move by "adding" that piece at the end position
            board.addPiece(endPos, piece);

            // set the start position of piece currently moving to empty
            board.removePiece(startPos);
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
        }

        // make sure we return this to normal before we leave the function
        getTeamMoves();
        return filteredMoves;
    }

    public void makeMove(ChessMove move) throws InvalidMoveException {
        // setup
        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();
        ChessPiece piece = board.getPiece(startPos);
        // verify that a piece exists here
        if (piece == null) {
            throw new InvalidMoveException("Invalid Move! There is no piece at that location.");
        }
        // verify piece color matches turn
        if (piece.getTeamColor() != turn) {
            throw new InvalidMoveException("Invalid Move! That piece does not belong to your team.");
        }
        Collection<ChessMove> validMoves = validMoves(startPos);

            // if we are not allowed to move here
        if (!validMoves.contains(move)) {
            throw new InvalidMoveException("Invalid Move!");
        }

        // this path means we have a move that works
        board.addPiece(endPos, piece); // overwrite our new position
        board.removePiece(startPos); // erase our old position

        if (lastOpponentMove != null && piece.getPieceType() == ChessPiece.PieceType.PAWN &&
                lastOpponentMove.getEndPosition().getRow() == move.getStartPosition().getRow() &&
                move.getEndPosition().getColumn() == lastOpponentMove.getEndPosition().getColumn()) {
            // pawn just moved, pawn move currently being made, they are on the same row,
            // AND the current pawn is moving to the column of the opposing pawn
            board.removePiece(lastOpponentMove.getEndPosition());
        }

        // if the pawn was being promoted, put that piece on the end position
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN && move.getPromotionPiece() != null) {
            board.addPiece(endPos, new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
        }

        // if king moved, update his position
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            if (turn == TeamColor.BLACK) {
                blackKingPosition = endPos;
            }
            else {
                whiteKingPosition = endPos;
            }
        }

        // now we need to update our position lists
        getTeamMoves();

        lastOpponentMove = move;

        // set game turn to other team
        if (turn == TeamColor.BLACK) {
            setTeamTurn(TeamColor.WHITE);
        }
        else {
            setTeamTurn(TeamColor.BLACK);
        }
    }

    public boolean isInCheck(TeamColor teamColor) {
        // setup
        Collection<ChessMove> opponentMoves;
        ChessPosition kingPosition;

        // need to see if current team color is in check, so declare what team are the opponents
        if (teamColor == TeamColor.BLACK) {
            opponentMoves = whiteMoves;
            kingPosition = blackKingPosition;

        }
        else {
            opponentMoves = blackMoves;
            kingPosition = whiteKingPosition;
        }

        // find out if the other team could move to your kings location
        for (ChessMove move : opponentMoves) {
            ChessPosition endPos = move.getEndPosition();
            if (endPos.equals(kingPosition)) {
                return true; // there is an opposing piece that can capture our king!
            }
        }
        // this path means that no pieces can capture our king. we are NOT in check
        return false;
    }

    public boolean isInCheckmate(TeamColor teamColor) {
        Collection<ChessMove> teamMoves;
        ChessGame.TeamColor oppositeColor;
        if (teamColor == TeamColor.WHITE) {
            teamMoves = new ArrayList<>(whiteMoves); // make a copy instead

        }
        else {
            teamMoves = new ArrayList<>(blackMoves); // make a copy instead
        }
        // loop to evaluate each move. if we are not in check after making a move, then return false
        for (ChessMove move : teamMoves) {
            // setup
            ChessPosition endPos = move.getEndPosition();
            ChessPosition startPos = move.getStartPosition();
            ChessPiece piece = board.getPiece(startPos);
            boolean isCapture = false;
            ChessPiece capturePiece;

            // if we are capturing a piece during our theoretical move, we need to record what it was to put it back afterward
            if (board.getPiece(endPos) != null && !board.getPiece(endPos).getTeamColor().equals(piece.getTeamColor())) {
                isCapture = true;
                capturePiece = new ChessPiece(board.getPiece(endPos).getTeamColor(), board.getPiece(endPos).getPieceType());
            }
            else {
                capturePiece = null;
            }

            // make move
            board.addPiece(endPos, piece);
            board.removePiece(startPos);

            // update opponent moves before evaluating check
            getTeamMoves();

            // see if we are still in check. if not, return false
            if (!isInCheck(teamColor)) {
                return false;
            }

            // restore board to old state and then loop over other pieces
            board.addPiece(startPos, piece);
            board.removePiece(endPos);
            if (isCapture) {
                board.addPiece(endPos, capturePiece);
            }

            getTeamMoves();
        }
        // after the for loop, if we are still in check, then this is checkmate
        return true;
    }

    public boolean isInStalemate(TeamColor teamColor) {
        // setup
        getTeamMoves();
        Collection<ChessPosition> positions;
        if (teamColor == TeamColor.BLACK) {
            positions = blackPositions;
        }
        else {
            positions = whitePositions;
        }

        // condition 1: you are not in check
        if (isInCheck(teamColor)) {
            return false;
        }

        // condition 2: you have no valid moves
        for (ChessPosition pos : positions) {
            if (!validMoves(pos).isEmpty()) {
                return false;
            }
        }

        // passed both conditions? return true
        return true;
    }

    public void setBoard(ChessBoard board) {
        this.board = board;
        getTeamMoves();
    }

    public ChessBoard getBoard() {
        return this.board;
    }
}
