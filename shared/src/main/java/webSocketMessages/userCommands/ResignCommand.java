package webSocketMessages.userCommands;

public class ResignCommand extends UserGameCommand{

    private Integer gameID;

    public ResignCommand(String authToken) {
        super(authToken);
        this.commandType = CommandType.RESIGN;
    }
    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }
    public Integer getGameID() {
        return gameID;
    }


}
