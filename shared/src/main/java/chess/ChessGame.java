package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor turn;
    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ChessGame chessGame = (ChessGame) object;
        return Objects.deepEquals(board, chessGame.board) && turn == chessGame.turn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, turn);
    }

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        turn  = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK,
        FINISHED
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */


    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece simPiece = board.getPiece(startPosition);
        Collection<ChessMove> simMoves = simPiece.pieceMoves(board, startPosition);
        Collection<ChessMove> isLegalMove = new ArrayList<>();


        if (simPiece == null) {
            return Collections.emptyList();
        }

        for (ChessMove moves : simMoves) {

            ChessPiece checkPiece = board.getPiece(moves.getEndPosition());
            board.addPiece(moves.getEndPosition(), simPiece);
            board.addPiece(moves.getStartPosition(), null);

            if (!isInCheck(simPiece.getTeamColor())) {
                isLegalMove.add(moves);
            }
            board.addPiece(moves.getStartPosition(), simPiece);
            board.addPiece(moves.getEndPosition(), checkPiece);
        }
        return isLegalMove;
    }


    private void changeTurn() {
        turn = (turn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece movingPiece = board.getPiece(move.getStartPosition());


        if (movingPiece == null || movingPiece.getTeamColor() != turn) {
            throw new InvalidMoveException("Invalid move.");
        }

        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (!validMoves.contains(move)) {
            throw new InvalidMoveException("Invalid move: Move not permitted.");
        }

        ChessPiece endPiece = board.getPiece(move.getEndPosition());
        board.addPiece(move.getEndPosition(), movingPiece);
        board.addPiece(move.getStartPosition(), null);

        if (isInCheck(turn)) {

            board.addPiece(move.getStartPosition(), movingPiece);
            board.addPiece(move.getEndPosition(), endPiece);
            throw new InvalidMoveException("Invalid move: King in Check.");
        }

        if (move.getPromotionPiece() != null) {
            board.addPiece(move.getEndPosition(), new ChessPiece(movingPiece.getTeamColor(), move.getPromotionPiece()));
        }

        changeTurn();
    }


    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    private ChessPosition findKing(TeamColor teamColor) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                ChessPiece piece = board.getPiece(new ChessPosition(r + 1, c + 1));
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    return new ChessPosition(r + 1, c + 1);
                }
            }
        }
        return null;
    }


    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition position = findKing(teamColor);
        int row;
        int col;
        if (position == null) {
            return false;
        }
        for(row = 0;row<8;row++){
            for (col = 0;col<8;col++){
                ChessPosition newPosition = new ChessPosition(row+1,col+1);
                ChessPiece newPiece = board.getPiece(newPosition);
                if(newPiece != null && newPiece.getTeamColor() != teamColor){
                    Collection<ChessMove> check = newPiece.pieceMoves(board,newPosition);

                    for(ChessMove move: check){

                        if(move.getEndPosition().equals(position)){
                            return true;
                        }
                    }
                }
            }
        }
        return false;

    }

    private Collection<ChessPosition> simAllPieces(TeamColor teamColor) {
        Collection<ChessPosition> allPossible = new ArrayList<>();
        int row;
        int col;
        for (row = 0; row < 8; row++) {
            for (col = 0; col < 8; col++) {

                ChessPiece newPiece = board.getPiece(new ChessPosition(row + 1, col + 1));

                if (newPiece != null && newPiece.getTeamColor() == teamColor) {

                    allPossible.add(new ChessPosition(row + 1, col + 1));
                }
            }
        }
        return allPossible;
    }
    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) return false;
        for (ChessPosition position : simAllPieces(teamColor)) {
            for (ChessMove move : validMoves(position)) {

                ChessPiece movingPiece = board.getPiece(move.getStartPosition());
                ChessPiece targetPos = board.getPiece(move.getEndPosition());
                board.addPiece(move.getEndPosition(), movingPiece);
                board.addPiece(move.getStartPosition(), null);

                if (!isInCheck(teamColor)) {
                    board.addPiece(move.getStartPosition(), movingPiece);
                    board.addPiece(move.getEndPosition(), targetPos);
                    return false;
                }

                board.addPiece(move.getStartPosition(), movingPiece);
                board.addPiece(move.getEndPosition(), targetPos);
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        Collection<ChessPosition> realAllPositions = simAllPieces(teamColor);
        for (ChessPosition newPosition : realAllPositions) {
            Collection<ChessMove> legalMove = validMoves(newPosition);

            if (!legalMove.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param newBoard the new board to use
     */
    public void setBoard(ChessBoard newBoard) {
        board = newBoard;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }


}