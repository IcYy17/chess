package webSocketMessages.userCommands;

public class ObserverCommand extends UserGameCommand{
    private Integer gameID;
    private String user;

    public ObserverCommand(String authToken) {
        super(authToken);
        this.commandType = CommandType.JOIN_OBSERVER;
    }
    public void setUsername(String username) {
        this.user = username;
    }

    public String getUsername() {
        return user;
    }
    public Integer getGameID() {
        return gameID;
    }

    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }




}
