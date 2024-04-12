package ui;
import exception.ResponseException;
import model.AuthInfo;
import model.GameInfo;
import chess.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

public class LoadGameBoard implements GameBoardHandler {
    private GameInfo gameData;
    private boolean isRunning = true;
    private ChessPosition highlightPosition = null;
    private Collection<ChessPosition> highlightMoves = null;
    private AuthInfo authData;
    private String color;
    private Boolean isObserver = false;
    private WebSocketFacade webSocketFacade;

    private Scanner scanner = new Scanner(System.in);
    private String url;


    //new code for websocket
    public LoadGameBoard (GameInfo gameData, String url, AuthInfo authData, String givenColor) throws ResponseException {
        this.gameData = gameData;
        this.url = url;
        this.authData = authData;
        if (givenColor != null) {
            this.color = givenColor;
            this.isObserver = false;
        } else {
            this.color = "white";
            this.isObserver = true;
        }
        this.webSocketFacade = new WebSocketFacade(url, this);
    }
    public void startGame() {
        Scanner inputReader = new Scanner(System.in);
        if (isObserver) {
            webSocketFacade.joinObserver(authData.authToken(), gameData.gameID(), authData.username());
        } else {
            webSocketFacade.joinPlayer(authData.authToken(), gameData.gameID(), authData.username(), convertTeamColor());
        }
        System.out.println(EscapeSequences.SET_TEXT_BOLD + "Welcome to " + gameData.gameName() + " game!" + EscapeSequences.RESET_TEXT_BOLD_FAINT + " (Type 'help' for a list of commands or 'quit' to exit the program.)");

        while (isRunning) {
            String playerColor = isObserver ? "Observer" : color;
            System.out.print(EscapeSequences.SET_TEXT_BOLD + playerColor + " >> " + EscapeSequences.RESET_TEXT_BOLD_FAINT);
            String userInput = inputReader.nextLine();
            String response = inputParser(userInput);
            System.out.println(response);
        }
    }

    public String inputParser(String input){
        var in = input.toLowerCase().split(" ");
        var cmd = (in.length > 0) ? in[0] : "help";
        var args = Arrays.copyOfRange(in, 1, in.length);
        return switch (cmd) {
            case "quit" -> quit();
            case "help" -> help();
            case "resign" -> resign();
            case "leave" -> leaveGame();
            case "redraw", "redrawing \n" -> displayGame(this.color);
            case "move" -> args.length != 2 ? "Invalid command. Use: move <from> <to>." : makeMove(input);
            case "highlight", "highlight legal moves" -> args.length != 1 ? "Invalid highlight command. Usage: highlight <position>." : highlightLegalMoves(args[0]);
            default -> help();
        };
    }

    private String help(){
        if (isObserver) {
            return "Available commands:\n" +
                    "help - You're looking at it...\n" +
                    "quit - Quit the program.\n" +
                    "Redraw - Redraw the board.\n" +
                    "Leave - Leave the current game.\n";
        } else {
            return "Available commands:\n" +
                    "redraw - Redraw the board.\n" +
                    "leave - Leave the game.\n" +
                    "help - You're looking at it...\n" +
                    "quit - Quit the program.\n" +

                    "move - Make a move. Use: move <from> <to>.\n" +
                    "resign - Resign the game.\n" +
                    "highlight - Highlight all legal moves for a piece. Use: highlight <position>.\n";
        }
    }

    private String leaveGame()
    {
        webSocketFacade.leaveGame(this.authData.authToken(), this.gameData.gameID());
        isRunning = false;
        return "You left the game.";
    }







    public String displayGame(String color) {
        if (this.gameData == null) {
            return "No game data available.";
        }

        var gameInfo = this.gameData.game();
        var turn = gameInfo.getTeamTurn();

        var output = "\nGame:\n";

        output += displayBlackBoard();
        return output + "\n" + "It is " + turn + "'s turn.\n";
    }


    private String displayBlackBoard() {
        var gameInfo = this.gameData.game();
        var board = gameInfo.getBoard();

        StringBuilder output = new StringBuilder(EscapeSequences.RESET_ALL);

        output.append(EscapeSequences.SET_TEXT_BOLD)
                .append(displaySides())
                .append(EscapeSequences.RESET_TEXT_BOLD_FAINT)
                .append("\n");

        for (int i = 7; i >= 0; i--) {
            output.append(EscapeSequences.SET_TEXT_BOLD)
                    .append(i +1)
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
                    .append(i +1)
                    .append(EscapeSequences.RESET_TEXT_BOLD_FAINT)
                    .append("\n");
        }

        output.append(EscapeSequences.SET_TEXT_BOLD)
                .append(displaySides())
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


    private String displaySides() {
        return "  \u2003a\u2003 b\u2003 c\u2003 d\u2003 e\u2003 f\u2003 g\u2003 h";
    }


}
