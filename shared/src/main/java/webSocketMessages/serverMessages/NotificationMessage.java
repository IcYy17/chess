package webSocketMessages.serverMessages;

public class NotificationMessage extends ServerMessage {

    private String notification;

    public NotificationMessage(ServerMessageType message) {
        super(message);
        this.serverMessageType = ServerMessageType.NOTIFICATION;
    }
    public void setMessage(String message) {
        this.notification = message;
    }
    public String getMessage() {
        return notification;
    }




}
