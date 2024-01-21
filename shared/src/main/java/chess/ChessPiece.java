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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return type == that.type && color == that.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, color);
    }

    private PieceType type;
    private ChessGame.TeamColor color;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
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
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> availableMoves = new ArrayList<>();
        switch (type) {
            case KING:
                availableMoves.addAll(kingMoveSet(board, myPosition));
                break;
//            case QUEEN:
//                return queenMoveSet(board, myPosition);
//            case ROOK:
//                return rookMoveSet(board, myPosition);
//            case KNIGHT:
//                return knightMoveSet(board, myPosition);
//            case BISHOP:
//                return bishopMoveSet(board, myPosition);
//            case PAWN:
//                return pawnMoveSet(board, myPosition);
        }
        return availableMoves;
    }
    private boolean isLegalMove(ChessBoard board, int row, int col){
        if (row > 8 || row < 1 || col > 8  || col < 1){
            return false;
        }
        ChessPiece square = board.getPiece(new ChessPosition(row, col));
        return square == null || square.getTeamColor() != this.color;
    }
        private Collection<ChessMove> kingMoveSet(ChessBoard board, ChessPosition myPosition){
            int [][] kingDirections = {{1,1},{1, -1}, {-1, 1},{-1,-1},{1, 0},{0,1},{0,-1},{-1,0}};
            Collection<ChessMove> legalMoves = new ArrayList<>();
            int row;
            int col;
            for (int [] move: kingDirections){
                row = myPosition.getRow() + move[0];
                col = myPosition.getColumn() + move[1];
                if(isLegalMove(board, row+1, col+1)){
                    legalMoves.add(new ChessMove(myPosition, new ChessPosition(row+1, col+1), null));

                }
            }
            return legalMoves;

        }

//        private Collection<ChessMove> queenMoveSet(ChessBoard board, ChessPosition myPosition) {
//            Collection<ChessMove> legalMoves = new ArrayList<>();
//
//        }



}
