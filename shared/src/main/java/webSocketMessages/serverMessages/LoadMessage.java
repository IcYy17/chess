package webSocketMessages.serverMessages;
import chess.ChessGame;
public class LoadMessage extends ServerMessage{
    private ChessGame game;
    private String whiteUsername;
    private String blackUsername;

    public LoadMessage(ServerMessageType type) {
        super(type);
        this.serverMessageType = ServerMessageType.LOAD_GAME;
    }
    public void setGame(ChessGame game) {
        this.game = game;
    }
    public ChessGame getGame() {
        return game;
    }
    public void setWhiteUser(String whiteUsername) {
        this.whiteUsername = whiteUsername;
    }

    public String getBlackUser() {
        return blackUsername;
    }


    public String getWhiteUser() {
        return whiteUsername;
    }



    public void setBlackUser(String blackUsername) {
        this.blackUsername = blackUsername;
    }

}
