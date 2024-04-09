package webSocketMessages.serverMessages;

public class ErrorMessage extends ServerMessage {

    private String errorNotification;

    public ErrorMessage(ServerMessageType message) {
        super(message);
        this.serverMessageType = ServerMessageType.ERROR;
    }

    public String getErrorMessage() {
        return errorNotification;
    }

    public void setErrorMessage(String message) {
        this.errorNotification = message;
    }
}
