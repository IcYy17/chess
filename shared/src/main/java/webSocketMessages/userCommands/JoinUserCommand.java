package webSocketMessages.userCommands;
import chess.ChessGame;
public class JoinUserCommand extends UserGameCommand {
    private String user;
    private Integer gameID;
    private ChessGame.TeamColor color;
    public JoinUserCommand(String authToken) {
        super(authToken);
        this.commandType = CommandType.JOIN_PLAYER;
    }
    public void setUsername(String username) {
        this.user = username;
    }

    public String getUsername() {
        return user;
    }

    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }
    public Integer getGameID() {
        return gameID;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return color;
    }

    public void setPlayerColor(ChessGame.TeamColor userColor) {
        this.color = userColor;
    }




}
