package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard chessBoard;
    private TeamColor turn;
    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ChessGame chessGame = (ChessGame) object;
        return Objects.deepEquals(chessBoard, chessGame.chessBoard) && turn == chessGame.turn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(chessBoard, turn);
    }

    public ChessGame() {
        chessBoard = new ChessBoard();
        chessBoard.resetBoard();
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
        turn = team;
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
        ChessPiece piece = chessBoard.getPiece(startPosition);
        if (piece != null) {
            return piece.pieceMoves(chessBoard, startPosition);
        }
        return null;
    }

    private void switchTurn() {
        turn = (turn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

        ChessPiece piece = chessBoard.getPiece(move.getStartPosition());

        if (piece != null && piece.getTeamColor() == turn) {

            Collection<ChessMove> validMoves = validMoves(move.getStartPosition());

            if (validMoves != null && validMoves.contains(move)) {

                if (move.getPromotionPiece() != null) {
                    ChessPiece promotedPiece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
                    this.chessBoard.addPiece(move.getEndPosition(), promotedPiece);
                }

                else {
                    this.chessBoard.addPiece(move.getEndPosition(), piece);
                }
                this.chessBoard.addPiece(move.getStartPosition(), null);

                if (isInCheck(turn)) {
                    this.chessBoard.addPiece(move.getEndPosition(), null);
                    this.chessBoard.addPiece(move.getStartPosition(), piece);
                    throw new InvalidMoveException("King is still in check");
                } // Check if the move puts the king in check

                switchTurn();
            }
            else {
                throw new InvalidMoveException("Invalid move");
            }
        }
        else {
            throw new InvalidMoveException("Invalid move");
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    private ChessPosition findKing(TeamColor teamColor){
        int row;
        int col;
        for (row = 0; row < 8; row++){
            for (col = 0; col <8; col++){
                ChessPiece allPieces = chessBoard.getPiece(new ChessPosition(row+1,col+1));
                if(allPieces !=null && allPieces.getPieceType() == ChessPiece.PieceType.KING && allPieces.getTeamColor() == teamColor){
                    return new ChessPosition(row+1,col+1);
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
                ChessPiece newPiece = chessBoard.getPiece(newPosition);
                if(newPiece != null && newPiece.getTeamColor() != teamColor){
                    Collection<ChessMove> checkMoves = newPiece.pieceMoves(chessBoard,newPosition);
                    for(ChessMove move: checkMoves){
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

                ChessPiece newPiece = chessBoard.getPiece(new ChessPosition(row + 1, col + 1));

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
        if (isInCheck(teamColor)) {
            Collection<ChessPosition> allPossible = simAllPieces(teamColor);

            for (ChessPosition newPosition : allPossible) {
                Collection<ChessMove> legalMoves = validMoves(newPosition);

                for (ChessMove move : legalMoves) {

                    ChessBoard simBoard = new ChessBoard();

                    ChessPiece simPiece = simBoard.getPiece(move.getStartPosition());
                    simBoard.addPiece(move.getEndPosition(), simPiece);
                    simBoard.addPiece(move.getStartPosition(), null);


                    if (!isInCheck(teamColor)) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
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
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        chessBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return chessBoard;
    }


}