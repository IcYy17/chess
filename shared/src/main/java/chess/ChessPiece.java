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
            case BISHOP:
                availableMoves.addAll(bishopMoveSet(board, myPosition));
                break;
            case PAWN:
                availableMoves.addAll(pawnMoveSet(board, myPosition));
                break;
            case KING:
                availableMoves.addAll(kingMoveSet(board, myPosition));
                break;
            case QUEEN:
                availableMoves.addAll(queenMoveSet(board, myPosition));
                break;
            case ROOK:
                availableMoves.addAll(rookMoveSet(board, myPosition));
                break;
            case KNIGHT:
                availableMoves.addAll(knightMoveSet(board, myPosition));
                break;

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


    private Collection<ChessMove> bishopMoveSet(ChessBoard board, ChessPosition myPosition) {
        int [][] bishopDirections = {{1,1}, {-1,1},{1, -1}, {-1, -1}};
        Collection<ChessMove> legalMoves = new ArrayList<>();
        int row;
        int col;
        int numSpaces;
        for (int [] move: bishopDirections){
            for(numSpaces = 1; numSpaces <=8; numSpaces++){
                row = myPosition.getRow() + move[0] * numSpaces;
                col = myPosition.getColumn() + move[1]*numSpaces;


                if( !isLegalMove(board, row +1, col +1)){
                    break;
                }
                legalMoves.add(new ChessMove(myPosition,new ChessPosition(row +1, col +1),null));
                ChessPiece pieceAlreadyThere = board.getPiece(new ChessPosition(row +1, col +1));
                if (pieceAlreadyThere != null){
                    break;
                }
            }
        }
        return legalMoves;
    }




    /**
     * pawns need a lot of work.
     */

    private Collection<ChessMove> pawnMoveSet(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> promo = new ArrayList<>();
        Collection<ChessMove> pawnMove = new ArrayList<>();

        int startRow = (color == ChessGame.TeamColor.WHITE) ? 1 : 6;
        int pointer = (color == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int promoRow = (color == ChessGame.TeamColor.WHITE) ? 6 : 1;


        int[] captureCols = {-1, 1};
        for (int captureCol : captureCols) {
            int captureRow = myPosition.getRow() + pointer;

            int captureColAdjusted = myPosition.getColumn() + captureCol;
            if (isLegalMove(board, captureRow + 1, captureColAdjusted + 1)) {
                ChessPiece pieceCap = board.getPiece(new ChessPosition(captureRow + 1, captureColAdjusted + 1));

                if (pieceCap != null && pieceCap.getTeamColor() != color) {
                    // Valid capture move
                    ChessMove captureMove = new ChessMove(myPosition, new ChessPosition(captureRow + 1, captureColAdjusted + 1), null);
                    pawnMove.add(captureMove);
                }
            }
        }

        int row = myPosition.getRow() + pointer;
        int col = myPosition.getColumn();

        if (isLegalMove(board, row + 1, col + 1)) {
            ChessPiece destinationPiece = board.getPiece(new ChessPosition(row + 1, col + 1));
            if (destinationPiece == null) {
                ChessMove move = new ChessMove(myPosition, new ChessPosition(row + 1, col + 1), null);

                pawnMove.add(move);


                if (startRow == myPosition.getRow() && color == ChessGame.TeamColor.WHITE) {

                    ChessMove doubleM = new ChessMove(myPosition, new ChessPosition(row + 2, col + 1), null);
                    ChessPiece chessP = board.getPiece(new ChessPosition(row + 2, col + 1));

                    if (chessP == null) {
                        pawnMove.add(doubleM);
                    }
                }

                if (startRow == myPosition.getRow() && color == ChessGame.TeamColor.BLACK) {

                    ChessMove doubleM = new ChessMove(myPosition, new ChessPosition(row, col + 1), null);

                    ChessPiece chessP = board.getPiece(new ChessPosition(row, col + 1));
                    if (chessP == null) {
                        pawnMove.add(doubleM);
                    }
                }
            }
        }


        if (myPosition.getRow() == promoRow) {
            for (ChessMove move : pawnMove) {
                promo.add(new ChessMove(myPosition, new ChessPosition(move.getEndPosition().getRow() + 1, move.getEndPosition().getColumn() + 1), ChessPiece.PieceType.BISHOP));
                promo.add(new ChessMove(myPosition, new ChessPosition(move.getEndPosition().getRow() + 1, move.getEndPosition().getColumn() + 1), ChessPiece.PieceType.QUEEN));
                promo.add(new ChessMove(myPosition, new ChessPosition(move.getEndPosition().getRow() + 1, move.getEndPosition().getColumn() + 1), ChessPiece.PieceType.KNIGHT));
                promo.add(new ChessMove(myPosition, new ChessPosition(move.getEndPosition().getRow() + 1, move.getEndPosition().getColumn() + 1), ChessPiece.PieceType.ROOK));

            }
        }

        if (promo.isEmpty()) {
            return pawnMove;
        }
        else{
            return promo;
        }
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

        private Collection<ChessMove> queenMoveSet(ChessBoard board, ChessPosition myPosition) {
            int [][] queenDirections = {{1,1}, {-1,1},{1, -1}, {-1, -1}, {-1, 0}, {1, 0}, {0,1}, {0,-1}};
            Collection<ChessMove> legalMoves = new ArrayList<>();
            int row;
            int col;
            int numSpaces;
            for (int [] move: queenDirections){
                for(numSpaces = 1; numSpaces <=8; numSpaces++){
                    row = myPosition.getRow() + move[0] * numSpaces;
                    col = myPosition.getColumn() + move[1]*numSpaces;


                    if( !isLegalMove(board, row +1, col +1)){
                        break;
                    }
                    legalMoves.add(new ChessMove(myPosition,new ChessPosition(row +1, col +1),null));
                    ChessPiece pieceAlreadyThere = board.getPiece(new ChessPosition(row +1, col +1));
                    if (pieceAlreadyThere != null){
                        break;
                    }
                }
            }
            return legalMoves;
        }
        private Collection<ChessMove> rookMoveSet(ChessBoard board, ChessPosition myPosition) {
            int [][] rookDirections = {{-1, 0}, {1, 0}, {0,1}, {0,-1}};
            Collection<ChessMove> legalMoves = new ArrayList<>();
            int row;
            int col;
            int numSpaces;
            for (int [] move: rookDirections){
                for(numSpaces = 1; numSpaces <=8; numSpaces++){
                    row = myPosition.getRow() + move[0] * numSpaces;
                    col = myPosition.getColumn() + move[1]*numSpaces;


                    if( !isLegalMove(board, row +1, col +1)){
                        break;
                    }
                    legalMoves.add(new ChessMove(myPosition,new ChessPosition(row +1, col +1),null));
                    ChessPiece pieceAlreadyThere = board.getPiece(new ChessPosition(row +1, col +1));
                    if (pieceAlreadyThere != null){
                        break;
                    }
                }
            }
            return legalMoves;

        }
    private Collection<ChessMove> knightMoveSet(ChessBoard board, ChessPosition myPosition) {
        int[][] knightDirections = {{2, 1}, {-1, 2}, {2, -1}, {-2, -1}, {1, 2}, {-2, 1}, {1, -2}, {-1, -2}};
        Collection<ChessMove> legalMoves = new ArrayList<>();
        int row;
        int col;
        int numSpaces;
        for (int[] move : knightDirections) {
            for (numSpaces = 1; numSpaces <= 8; numSpaces++) {
                row = myPosition.getRow() + move[0];
                col = myPosition.getColumn() + move[1];

                if (!isLegalMove(board, row + 1, col + 1)) {
                    break;
                }
                legalMoves.add(new ChessMove(myPosition, new ChessPosition(row + 1, col + 1), null));
                ChessPiece pieceAlreadyThere = board.getPiece(new ChessPosition(row + 1, col + 1));
                if (pieceAlreadyThere != null) {
                    break;
                }

                if (isLegalMove(board, row, col) && (pieceAlreadyThere == null || pieceAlreadyThere.getTeamColor() != this.color)) {
                    legalMoves.add(new ChessMove(myPosition, new ChessPosition(row + 1, col + 1), null));
                }
            }

        }
        return legalMoves;
    }




}
