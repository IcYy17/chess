package webSocketMessages.serverMessages;
import chess.ChessGame;
public class LoadMessage extends ServerMessage{
    private ChessGame game;
    private String whiteUser;
    private String blackUser;

    public LoadMessage(ServerMessageType message) {
        super(message);
        this.serverMessageType = ServerMessageType.LOAD_GAME;
    }
    public void setGame(ChessGame game) {
        this.game = game;
    }
    public ChessGame getGame() {
        return game;
    }
    public void setWhiteUser(String whiteUsername) {
        this.whiteUser = whiteUsername;
    }

    public String getBlackUser() {
        return blackUser;
    }


    public String getWhiteUser() {
        return whiteUser;
    }



    public void setBlackUser(String blackUsername) {
        this.blackUser = blackUsername;
    }

}
