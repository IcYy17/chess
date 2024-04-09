package webSocketMessages.userCommands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {
    private Integer gameID;
    private ChessMove makeMove;


    public MakeMoveCommand(String authToken) {
        super(authToken);
        this.commandType = CommandType.MAKE_MOVE;
    }


    public Integer getGameID() {
        return gameID;
    }

    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }

    public String getAuthToken() {
        return getAuthString();
    }
    public ChessMove getMove() {
        return makeMove;
    }

    public void setMove(ChessMove move) {
        this.makeMove = move;
    }
}
