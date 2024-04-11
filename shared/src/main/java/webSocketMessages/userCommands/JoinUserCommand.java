package webSocketMessages.userCommands;
import chess.ChessGame;

public class JoinUserCommand extends UserGameCommand {
    private String username;
    private Integer gameID;

    private ChessGame.TeamColor playerColor;
    public JoinUserCommand(String authToken) {
        super(authToken);
        this.commandType = CommandType.JOIN_PLAYER;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }
    public Integer getGameID() {
        return gameID;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(ChessGame.TeamColor playerColor) {
        this.playerColor = playerColor;
    }



}
