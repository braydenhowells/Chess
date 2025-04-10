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

                // update the king position if applicable
                if (boardPiece.getPieceType() == ChessPiece.PieceType.KING) {
                    if (boardPiece.getTeamColor() == TeamColor.BLACK) {
                        blackKingPosition = boardPosition;
                    } else {
                        whiteKingPosition = boardPosition;
                    }
                }

                // this is a real piece. see what color it is and add to list
                if (boardPiece.getTeamColor() == TeamColor.BLACK) {
                    blackMoves.addAll(boardPiece.pieceMoves(board, boardPosition));
                    blackPositions.add(boardPosition);
                }
                if (boardPiece.getTeamColor() == TeamColor.WHITE) {
                    whiteMoves.addAll(boardPiece.pieceMoves(board, boardPosition));
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
        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> filteredMoves = new ArrayList<>();
        ChessGame.TeamColor pieceColor = piece.getTeamColor();

        // en passant
        if (lastOpponentMove != null) {
            if ((board.getPiece(lastOpponentMove.getEndPosition()).getPieceType() == ChessPiece.PieceType.PAWN) &&
                    piece.getPieceType() == ChessPiece.PieceType.PAWN) {
                checkEnPassant(lastOpponentMove, possibleMoves, pieceColor, startPosition);
            }
        }

        for (ChessMove move : possibleMoves) {
            boolean isCapture = false;
            ChessPosition endPos = move.getEndPosition();
            ChessPosition startPos = move.getStartPosition();
            ChessPiece capturePiece;

            if (board.getPiece(endPos) != null && !board.getPiece(endPos).getTeamColor().equals(piece.getTeamColor())) {
                isCapture = true;
                capturePiece = new ChessPiece(board.getPiece(endPos).getTeamColor(), board.getPiece(endPos).getPieceType());
            } else {
                capturePiece = null;
            }

            board.addPiece(endPos, piece);
            board.removePiece(startPos);
            getTeamMoves();

            if (!isInCheck(pieceColor)) {
                filteredMoves.add(move);
            }

            board.addPiece(startPos, piece);
            board.removePiece(endPos);
            if (isCapture) {
                board.addPiece(endPos, capturePiece);
            }
        }

        getTeamMoves();
        return filteredMoves;
    }

    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();
        ChessPiece piece = board.getPiece(startPos);

        if (piece == null) {
            throw new InvalidMoveException("Invalid Move! There is no piece at that location.");
        }
        if (piece.getTeamColor() != turn) {
            throw new InvalidMoveException("Invalid Move! That piece does not belong to your team.");
        }

        Collection<ChessMove> validMoves = validMoves(startPos);
        if (!validMoves.contains(move)) {
            throw new InvalidMoveException("Invalid Move!");
        }

        board.addPiece(endPos, piece);
        board.removePiece(startPos);

        if (lastOpponentMove != null && piece.getPieceType() == ChessPiece.PieceType.PAWN &&
                lastOpponentMove.getEndPosition().getRow() == move.getStartPosition().getRow() &&
                move.getEndPosition().getColumn() == lastOpponentMove.getEndPosition().getColumn()) {
            board.removePiece(lastOpponentMove.getEndPosition());
        }

        if (piece.getPieceType() == ChessPiece.PieceType.PAWN && move.getPromotionPiece() != null) {
            board.addPiece(endPos, new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
        }

        // ✅ Fix: use actual team color, not turn
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            if (piece.getTeamColor() == TeamColor.BLACK) {
                blackKingPosition = endPos;
            } else {
                whiteKingPosition = endPos;
            }
        }

        getTeamMoves();
        lastOpponentMove = move;

        if (turn == TeamColor.BLACK) {
            setTeamTurn(TeamColor.WHITE);
        } else {
            setTeamTurn(TeamColor.BLACK);
        }
    }

    public boolean isInCheck(TeamColor teamColor) {
        Collection<ChessMove> opponentMoves;
        ChessPosition kingPosition;

        if (teamColor == TeamColor.BLACK) {
            opponentMoves = whiteMoves;
            kingPosition = blackKingPosition;
        } else {
            opponentMoves = blackMoves;
            kingPosition = whiteKingPosition;
        }

        if (kingPosition == null) {
            System.err.println("⚠️ No king found for " + teamColor + " — skipping check detection.");
            return false;
        }

        for (ChessMove move : opponentMoves) {
            if (move.getEndPosition().equals(kingPosition)) {
                return true;
            }
        }
        return false;
    }

    public boolean isInCheckmate(TeamColor teamColor) {
        Collection<ChessMove> teamMoves;
        if (teamColor == TeamColor.WHITE) {
            teamMoves = new ArrayList<>(whiteMoves);
        } else {
            teamMoves = new ArrayList<>(blackMoves);
        }

        for (ChessMove move : teamMoves) {
            ChessPosition endPos = move.getEndPosition();
            ChessPosition startPos = move.getStartPosition();
            ChessPiece piece = board.getPiece(startPos);
            boolean isCapture = false;
            ChessPiece capturePiece;

            if (board.getPiece(endPos) != null && !board.getPiece(endPos).getTeamColor().equals(piece.getTeamColor())) {
                isCapture = true;
                capturePiece = new ChessPiece(board.getPiece(endPos).getTeamColor(), board.getPiece(endPos).getPieceType());
            } else {
                capturePiece = null;
            }

            board.addPiece(endPos, piece);
            board.removePiece(startPos);
            getTeamMoves();

            if (!isInCheck(teamColor)) {
                board.addPiece(startPos, piece);
                board.removePiece(endPos);
                if (isCapture) {
                    board.addPiece(endPos, capturePiece);
                }
                getTeamMoves();
                return false;
            }

            board.addPiece(startPos, piece);
            board.removePiece(endPos);
            if (isCapture) {
                board.addPiece(endPos, capturePiece);
            }

            getTeamMoves();
        }
        return true;
    }

    public boolean isInStalemate(TeamColor teamColor) {
        getTeamMoves();
        Collection<ChessPosition> positions;
        if (teamColor == TeamColor.BLACK) {
            positions = new ArrayList<>(blackPositions);
        } else {
            positions = new ArrayList<>(whitePositions);
        }

        if (isInCheck(teamColor)) {
            return false;
        }

        for (ChessPosition pos : positions) {
            if (!validMoves(pos).isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public void setBoard(ChessBoard board) {
        this.board = board;
        getTeamMoves();
    }

    public ChessBoard getBoard() {
        return this.board;
    }

    public ChessPosition getWhiteKingPosition() {
        return whiteKingPosition;
    }

    public ChessPosition getBlackKingPosition() {
        return blackKingPosition;
    }
}
