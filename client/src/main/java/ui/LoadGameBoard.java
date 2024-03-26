package ui;
import model.GameInfo;
import chess.*;

public class LoadGameBoard {
    private GameInfo gameData;
    private ChessBoard board;


    public LoadGameBoard(GameInfo gameData) {
        this.gameData = gameData;
        this.board = new ChessBoard();
        board.resetBoard();
    }

    public String displayGame(String color) {

        String gameIntro = "Game " + this.gameData.gameName() + ":\n";

        String firstBoardDisplay = color.equalsIgnoreCase("white") ? displayWhiteBoard() : displayBlackBoard();
        String secondBoardDisplay = color.equalsIgnoreCase("white") ? displayBlackBoard() : displayWhiteBoard();

        String output = gameIntro + firstBoardDisplay + "\n--------------------------------\n" + secondBoardDisplay + "\nIt is white's turn.";

        return output;
    }


    private String displayWhiteBoard() {
        StringBuilder output = new StringBuilder(EscapeSequences.RESET_ALL);

        output.append(EscapeSequences.SET_TEXT_BOLD)
                .append(displaySides(false))
                .append(EscapeSequences.RESET_TEXT_BOLD_FAINT)
                .append("\n");

        for (int i = 0; i < 8; i++) {
            output.append(EscapeSequences.SET_TEXT_BOLD)
                    .append(8 - i)
                    .append(EscapeSequences.RESET_TEXT_BOLD_FAINT)
                    .append(" ");

            for (int j = 0; j < 8; j++) {
                var piece = board.getPiece(new ChessPosition(i + 1, j + 1));
                boolean isEvenSquare = (i + j) % 2 == 0;

                String bgColor = isEvenSquare ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.SET_BG_COLOR_DARK_GREY;
                String fgColor = piece != null && piece.getTeamColor().toString().equals("WHITE") ? EscapeSequences.SET_TEXT_COLOR_WHITE : EscapeSequences.SET_TEXT_COLOR_BLACK;

                output.append(bgColor)
                        .append(fgColor)
                        .append(displayPiece(piece))
                        .append(EscapeSequences.RESET_BG_COLOR)
                        .append(EscapeSequences.RESET_TEXT_COLOR);
            }

            output.append(EscapeSequences.SET_BG_COLOR_DARK_GREY)
                    .append(EscapeSequences.SET_TEXT_BOLD)
                    .append(8 - i)
                    .append(EscapeSequences.RESET_TEXT_BOLD_FAINT)
                    .append("\n");
        }

        output.append(EscapeSequences.SET_TEXT_BOLD)
                .append(displaySides(false))
                .append(EscapeSequences.RESET_TEXT_BOLD_FAINT)
                .append(EscapeSequences.SET_BG_COLOR_DARK_GREY)
                .append(EscapeSequences.RESET_ALL);
        output.append(EscapeSequences.RESET_TEXT_COLOR).append(EscapeSequences.RESET_TEXT_BOLD_FAINT);

        return output.toString();
    }



    private String displayBlackBoard() {
        StringBuilder output = new StringBuilder(EscapeSequences.RESET_ALL);

        output.append(EscapeSequences.SET_TEXT_BOLD)
                .append(displaySides(true))
                .append(EscapeSequences.RESET_TEXT_BOLD_FAINT)
                .append("\n");

        for (int i = 7; i >= 0; i--) {
            output.append(EscapeSequences.SET_TEXT_BOLD)
                    .append(8 - i)
                    .append(EscapeSequences.RESET_TEXT_BOLD_FAINT)
                    .append(" ");

            for (int j = 7; j >= 0; j--) {
                var piece = board.getPiece(new ChessPosition(i + 1, j + 1));
                boolean isEvenSquare = (i + j) % 2 == 0;
                String bgColor = isEvenSquare ? EscapeSequences.SET_BG_COLOR_DARK_GREY : EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
                String fgColor = piece != null && piece.getTeamColor().toString().equals("WHITE") ? EscapeSequences.SET_TEXT_COLOR_WHITE : EscapeSequences.SET_TEXT_COLOR_BLACK;

                output.append(bgColor)
                        .append(fgColor)
                        .append(displayPiece(piece))
                        .append(EscapeSequences.RESET_BG_COLOR)
                        .append(EscapeSequences.RESET_TEXT_COLOR);
            }

            output.append(EscapeSequences.SET_BG_COLOR_DARK_GREY)
                    .append(EscapeSequences.SET_TEXT_BOLD)
                    .append(8 - i)
                    .append(EscapeSequences.RESET_TEXT_BOLD_FAINT)
                    .append("\n");
        }

        output.append(EscapeSequences.SET_TEXT_BOLD)
                .append(displaySides(true))
                .append(EscapeSequences.RESET_TEXT_BOLD_FAINT)
                .append(EscapeSequences.SET_BG_COLOR_DARK_GREY)
                .append(EscapeSequences.RESET_ALL);
        output.append(EscapeSequences.RESET_TEXT_COLOR).append(EscapeSequences.RESET_TEXT_BOLD_FAINT);

        return output.toString();
    }



    private String displayPiece(ChessPiece piece) {
        if (piece == null) {
            return EscapeSequences.EMPTY;
        }

        String pieceColor = piece.getTeamColor().toString().equals("WHITE") ? EscapeSequences.SET_TEXT_COLOR_WHITE : EscapeSequences.SET_TEXT_COLOR_BLACK;
        String resetColor = EscapeSequences.SET_TEXT_COLOR_WHITE;

        String pieceSymbol;
        switch (piece.getPieceType()) {
            case PAWN:
                pieceSymbol = piece.getTeamColor().toString().equals("WHITE") ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
                break;
            case ROOK:
                pieceSymbol = piece.getTeamColor().toString().equals("WHITE") ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
                break;
            case KNIGHT:
                pieceSymbol = piece.getTeamColor().toString().equals("WHITE") ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
                break;
            case BISHOP:
                pieceSymbol = piece.getTeamColor().toString().equals("WHITE") ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
                break;
            case QUEEN:
                pieceSymbol = piece.getTeamColor().toString().equals("WHITE") ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
                break;
            case KING:
                pieceSymbol = piece.getTeamColor().toString().equals("WHITE") ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
                break;
            default:
                pieceSymbol = EscapeSequences.EMPTY;
        }

        return pieceColor + pieceSymbol + resetColor;
    }


    private String displaySides(Boolean inverted) {
        return inverted ? "  \u2003h\u2003 g\u2003 f\u2003 e\u2003 d\u2003 c\u2003 b\u2003 a" : "  \u2003a\u2003 b\u2003 c\u2003 d\u2003 e\u2003 f\u2003 g\u2003 h";
    }


}
