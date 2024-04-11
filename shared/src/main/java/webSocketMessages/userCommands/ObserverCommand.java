package webSocketMessages.userCommands;

public class ObserverCommand extends UserGameCommand{
    private Integer gameID;
    private String username;

    public ObserverCommand(String authToken) {
        super(authToken);
        this.commandType = CommandType.JOIN_OBSERVER;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
    public Integer getGameID() {
        return gameID;
    }

    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }




}
