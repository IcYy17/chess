package webSocketMessages.userCommands;

public class ExitGameCommand extends UserGameCommand {

    private Integer gameID;

    public ExitGameCommand(String authToken) {
        super(authToken);
        this.commandType = CommandType.LEAVE;
    }
    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }
    public Integer getGameID() {
        return gameID;
    }





}
