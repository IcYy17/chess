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
            webSocketFacade.joinPlayer(authData.authToken(), gameData.gameID(), authData.username(), convertColor());
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
            case "highlight", "highlight legal moves" -> args.length != 1 ? "Invalid highlight command. Usage: highlight <position>." : highlightMoves(args[0]);
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

    private String makeMove(String move) {
        if (gameData.game().getTeamTurn() != convertColor()) {
            return "It is not your turn.";
        }

        String[] moveParts = move.split(" ");
        String start = moveParts[1];
        String end = moveParts[2];
        if (!start.matches("[a-h][1-8]") || !end.matches("[a-h][1-8]")) {
            return "Invalid move. Please enter a move in the format 'move <from> <to>' where <from> and <to> are positions on the board in the format 'a1' to 'h8'.";
        }

        ChessPosition startPosition = convertToRealPosition(start);
        ChessPosition endPosition = convertToRealPosition(end);
        Collection<ChessMove> possibleMoves = gameData.game().validMoves(startPosition);
        if (possibleMoves == null || !possibleMoves.contains(new ChessMove(startPosition, endPosition, null))) {
            return "Invalid move. Please enter a move in the format 'move <from> <to>'.";
        }

        ChessPiece.PieceType piecePromotion = null;
        if (gameData.game().getBoard().getPiece(startPosition).getPieceType() == ChessPiece.PieceType.PAWN && (endPosition.getRow() == 0 || endPosition.getRow() == 7)) {
            System.out.println("What would you like to promote to? (queen, rook, bishop, knight)");
            try (Scanner inputScanner = new Scanner(System.in)) {
                String promotionChoice = inputScanner.nextLine();
                switch (promotionChoice) {
                    case "queen":
                        piecePromotion = ChessPiece.PieceType.QUEEN;
                        break;
                    case "rook":
                        piecePromotion = ChessPiece.PieceType.ROOK;
                        break;
                    case "bishop":
                        piecePromotion = ChessPiece.PieceType.BISHOP;
                        break;
                    case "knight":
                        piecePromotion = ChessPiece.PieceType.KNIGHT;
                        break;
                    default:
                        return "Invalid promotion. Please try again.";
                }
            }
        }

        ChessMove moveExecuted = new ChessMove(startPosition, endPosition, piecePromotion);
        webSocketFacade.makeMove(authData.authToken(), gameData.gameID(), moveExecuted);
        return "You have made the move " + start + " to " + end + ".";
    }


    private String resign() {
        if (isObserver) {
            return "Observers cannot resign. Please leave the game instead.";
        }

        ChessGame.TeamColor currentTurn = gameData.game().getTeamTurn();
        if (currentTurn == ChessGame.TeamColor.FINISHED) {
            return "The game has already ended.";
        }

        boolean isOtherPlayerAbsent = (Objects.equals(this.color, "white") && gameData.blackUsername() == null) ||
                (Objects.equals(this.color, "black") && gameData.whiteUsername() == null);
        if (isOtherPlayerAbsent) {
            return "The other player has already resigned or left the game.";
        }


        System.out.println("Are you sure you want to resign? (yes/no)");
        String userResponse = scanner.nextLine();
        if (!userResponse.equalsIgnoreCase("yes") && !userResponse.equalsIgnoreCase("y")) {
            return "Resignation cancelled.";
        }

        resignGame();
        return "You have resigned the game.";
    }


    private void resignGame(){
        isRunning = false;
        webSocketFacade.resignGame(this.authData.authToken(), this.gameData.gameID());
    }

    private String quit(){
        isRunning = false;
        if (this.gameData != null) {
            webSocketFacade.leaveGame(this.authData.authToken(), this.gameData.gameID());
        }
        webSocketFacade.onClose();
        return "Come back Soon!";
    }

    private ChessPosition convertToRealPosition(String input) {
        char columnChar = input.charAt(0);
        int row = Character.getNumericValue(input.charAt(1));
        int column = 8 - (columnChar - 'a');
        return new ChessPosition(row, column);
    }

    private String highlightMoves(String position){

        if (this.gameData.game().getTeamTurn() != convertColor()) {
            return "It is not your turn.";
        }
        var pos = convertToRealPosition(position);
        if (pos == null) {
            return "Invalid position. Please try again.";
        }
        var validMoves = gameData.game().validMoves(pos);
        if (validMoves == null) {
            return "Invalid position. Please try again.";
        }
        var output = "Legal moves for " + position + ": ";
        this.highlightPosition = pos;
        this.highlightMoves = validMoves.stream().map(move -> move.getEndPosition()).collect(Collectors.toList());
        output = displayBlackBoard();
        this.highlightPosition = null;
        this.highlightMoves = null;
        return output;
    }
    private ChessGame.TeamColor convertColor(){
        if (this.color == null) {
            return null;
        }

        if (this.color.equalsIgnoreCase("white")) {
            return ChessGame.TeamColor.WHITE;
        } else {
            return ChessGame.TeamColor.BLACK;
        }
    }

    @Override
    public void updateGame(ChessGame game, String whiteUsername, String blackUsername) {
        var userColor = Boolean.TRUE.equals(isObserver) ? "Observer" : this.color;
        this.gameData = new GameInfo(this.gameData.gameID(), whiteUsername, blackUsername, this.gameData.gameName(), game);
        System.out.println("\nGameUpdate\n" + displayGame(this.color));
        System.out.print(EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_YELLOW + userColor + " >> " + EscapeSequences.RESET_TEXT_BOLD_FAINT + EscapeSequences.RESET_TEXT_COLOR);
    }

    @Override
    public void printMessage(String message) {
        var userColor = isObserver ? "Observer" : this.color;
        System.out.println("\nINCOMING MESSAGE >>>> " + message);
        System.out.print(EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_YELLOW + userColor + " >> " + EscapeSequences.RESET_TEXT_BOLD_FAINT + EscapeSequences.RESET_TEXT_COLOR);
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
        ChessGame gameInfo = this.gameData.game();
        ChessBoard board = gameInfo.getBoard();

        StringBuilder output = new StringBuilder();
        output.append(EscapeSequences.SET_TEXT_BOLD)
                .append(displaySides())
                .append(EscapeSequences.RESET_TEXT_BOLD_FAINT)
                .append("\n");

        for (int i = 7; i >= 0; i--) {
            output.append(EscapeSequences.SET_TEXT_BOLD)
                    .append(i + 1)
                    .append(EscapeSequences.RESET_TEXT_BOLD_FAINT)
                    .append(" ");

            for (int j = 7; j >= 0; j--) {
                ChessPiece piece = board.getPiece(new ChessPosition(i + 1, j + 1));
                if (highlightPosition != null && highlightPosition.getRow() == i && highlightPosition.getColumn() == j) {
                    output.append(EscapeSequences.SET_BG_COLOR_YELLOW)
                            .append(displayPiece(piece))
                            .append(EscapeSequences.SET_BG_COLOR_DARK_GREY);
                } else if (highlightMoves != null && highlightMoves.contains(new ChessPosition(i + 1, j + 1))) {
                    output.append(EscapeSequences.SET_BG_COLOR_GREEN)
                            .append(displayPiece(piece))
                            .append(EscapeSequences.SET_BG_COLOR_DARK_GREY);
                } else {
                    output.append((i + j) % 2 == 0 ? EscapeSequences.SET_BG_COLOR_DARK_GREY : EscapeSequences.SET_BG_COLOR_RED)
                            .append(displayPiece(piece));
                }
            }
            output.append(EscapeSequences.SET_BG_COLOR_DARK_GREY)
                    .append(EscapeSequences.SET_TEXT_BOLD)
                    .append(i + 1)
                    .append(EscapeSequences.RESET_TEXT_BOLD_FAINT)
                    .append("\n");
        }

        output.append(EscapeSequences.SET_TEXT_BOLD)
                .append(displaySides())
                .append(EscapeSequences.RESET_TEXT_BOLD_FAINT)
                .append(EscapeSequences.SET_BG_COLOR_DARK_GREY)
                .append(EscapeSequences.RESET_ALL);

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
