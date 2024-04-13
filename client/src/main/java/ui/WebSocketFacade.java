package ui;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import webSocketMessages.serverMessages.*; //need to add more here

import webSocketMessages.userCommands.*; //need to add more here

import chess.ChessGame;
import chess.ChessMove;
import exception.ResponseException;
public class WebSocketFacade extends Endpoint {

    private Session session;
    private LoadGameBoard game;

    @Override
    public void onOpen(Session session, EndpointConfig config) {
    }

    @OnClose
    public void onClose(){
    }

    @OnError
    public void onError(){
    }

    public WebSocketFacade(String url, LoadGameBoard game) throws ResponseException {
        try {
            this.game = game;
            url = url.replace("http://", "ws://") + "/connect";
            WebSocketContainer container=ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, new URI(url));
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    receivedMessage(message);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException e) {
            throw new ResponseException(500, "Failed: 500 Failed to connect to the server");
        }
    }

    //changed
    public void joinPlayer(String authToken, Integer gameID, String username, ChessGame.TeamColor playerColor) {
        var joinUserCommand = new JoinUserCommand(authToken);
        joinUserCommand.setGameID(gameID);
        joinUserCommand.setUsername(username);
        joinUserCommand.setPlayerColor(playerColor);
        sendMessage(joinUserCommand);
    }

    public void joinObserver(String authToken, Integer gameID, String username) {
        var obs = new ObserverCommand(authToken);
        obs.setGameID(gameID);
        obs.setUsername(username);
        sendMessage(obs);
    }

    //changed move to makeMove - game works but tests fail...
    public void makeMove(String authToken, Integer gameID, ChessMove move) {
        var makeMoveCommand = new MakeMoveCommand(authToken);
        makeMoveCommand.setGameID(gameID);
        makeMoveCommand.setMove(move);
        sendMessage(makeMoveCommand);
    }

    public void leaveGame(String authToken, Integer gameID) {
        var leave = new ExitGameCommand(authToken);
        leave.setGameID(gameID);
        sendMessage(leave);
    }

    public void resignGame(String authToken, Integer gameID) {
        var resign = new ResignCommand(authToken);
        resign.setGameID(gameID);
        sendMessage(resign);
    }

    public void receivedMessage(String message) {
        var gs = new Gson();
        var jsonPart = gs.fromJson(message, JsonElement.class);
        var jsonObject = jsonPart.getAsJsonObject();
        var messagePart = jsonObject.get("serverMessageType").getAsString();

        switch (messagePart) {
            case "LOAD_GAME":
                var gameMessage = gs.fromJson(jsonObject, LoadMessage.class);
                game.updateGame(gameMessage.getGame(), gameMessage.getWhiteUser(), gameMessage.getBlackUser());
                break;
            case "NOTIFICATION":
                var notification = gs.fromJson(jsonObject, NotificationMessage.class);
                game.printMessage(notification.getMessage());
                break;
            case "ERROR":
                var error = gs.fromJson(jsonObject, ErrorMessage.class);
                game.printMessage(error.getErrorMessage());
                break;
            default:
                System.out.println("Unknown: " + messagePart);
                break;
        }
    }

    private void sendMessage(Object message) {
        if (this.session != null && this.session.isOpen()) {
            try {
                this.session.getBasicRemote().sendText(new Gson().toJson(message));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: Session is either null or closed.");
        }
    }




}
