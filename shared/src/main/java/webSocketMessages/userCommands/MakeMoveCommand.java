package webSocketMessages.userCommands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {
    private Integer gameID;

    //move wont work but works for tests, makeMove works but not for tests
    private ChessMove move;


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
        return move;
    }

    public void setMove(ChessMove move) {
        this.move = move;
    }
}
