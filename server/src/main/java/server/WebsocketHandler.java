package server;

import chess.ChessGame;
import com.google.gson.Gson;

import chess.ChessGame.TeamColor;
import dataAccess.DataAccessException;
import exception.ResponseException;
import model.GameInfo;

import org.eclipse.jetty.websocket.api.*;

import org.eclipse.jetty.websocket.api.annotations.*;
import webSocketMessages.serverMessages.*;
import webSocketMessages.userCommands.*;

import webSocketMessages.userCommands.UserGameCommand;
import webSocketMessages.serverMessages.ServerMessage;
import service.*;
import java.io.IOException;
import java.util.Objects;

@WebSocket
public class WebsocketHandler {
    private WebsocketSession sessions = new WebsocketSession();
    private WebSocketService service = new WebSocketService();
    @OnWebSocketConnect
    public void onConnect(Session session) throws IOException {
        System.out.println("Connected");
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("Closed: " + statusCode + " " + reason);
        sessions.removeSession(session);
        session.close();
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws ResponseException, IOException {
        System.out.println("Message: " + message);
        UserGameCommand cmd = new Gson().fromJson(message, UserGameCommand.class);
        Gson gson = new Gson();
        switch (cmd.getCommandType()) {
            case JOIN_PLAYER:
                JoinUserCommand join = gson.fromJson(message, JoinUserCommand.class);
                joinPlayer(join, session);
                break;
//            case JOIN_OBSERVER:
//                ObserverCommand observer = gson.fromJson(message, ObserverCommand.class);
//                joinObserver(observer, session);
//                break;
//            case MAKE_MOVE:
//                //changed to makeMoveCommand from move
//                MakeMoveCommand makeMoveCommand = gson.fromJson(message, MakeMoveCommand.class);
//                makeMove(makeMoveCommand, session);
//                break;
//            case LEAVE:
//                ExitGameCommand exit = gson.fromJson(message, ExitGameCommand.class);
//                leaveGame(exit, session);
//                break;
//            case RESIGN:
//                ResignCommand resign = gson.fromJson(message, ResignCommand.class);
//                resignGame(resign, session);
//                break;
            default:
                throw new ResponseException(500, "Invalid command type");
        }
    }
    // changed
    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        Gson gson = new Gson();
        String errorMessageText = "Error: " + error.getMessage();
        System.out.println(errorMessageText);
        ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR);
        errorMessage.setErrorMessage(errorMessageText);
        String jsonErrorMessage = gson.toJson(errorMessage);
        System.out.println(jsonErrorMessage);
        try {
            session.getRemote().sendString(jsonErrorMessage);
        } catch (IOException e) {
            System.out.println("Error sending error message: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private <T extends UserGameCommand> void setUsernameFromAuthToken(T command, Session session) {
        try {

            if(command.getCommandType() == UserGameCommand.CommandType.JOIN_PLAYER)
                ((JoinUserCommand) command).setUsername(service.getUsernameFromAuthToken(command.getAuthString()));

            else if(command.getCommandType() == UserGameCommand.CommandType.JOIN_OBSERVER)
                ((ObserverCommand) command).setUsername(service.getUsernameFromAuthToken(command.getAuthString()));

        } catch (DataAccessException e) {
            onError(session, e);
        }
    }
    private void sendMessagesForJoinAndObserve(ChessGame game, Integer gameID, String authToken, Session session, String message, String whiteUsername, String blackUsername) {
        // Send a LoadGameMessage to the player
        var load = new LoadMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        load.setGame(game);
        load.setWhiteUser(whiteUsername);
        load.setBlackUser(blackUsername);

        try {
            sendMessage(gameID, load, authToken, session);
        } catch (ResponseException | IOException e) {
            onError(session, e);
            return;
        }

        // Send a NotificationMessage to the other players
        var notify=new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notify.setMessage(message);
        try {
            broadcastMessage(gameID, notify, authToken);

        } catch (IOException e) {
            onError(session, e);
            return;
        }
    }
    private void sendMessage(Integer gameID, ServerMessage message, String authToken, Session session)
            throws ResponseException, IOException {

        if (session != null) {
            if (session.isOpen()) {
                String newJsonMessage = new Gson().toJson(message);
                System.out.println("Sending message to session: " + session + ", message: " + newJsonMessage);
                session.getRemote().sendString(newJsonMessage);
                session.getRemote().flush();
            } else {
                System.out.println("Cannot send message. Session is closed.");
            }
        } else {
            System.out.println("Cannot send message. Session is null.");
        }
    }

    private void broadcastMessage(Integer gameID, ServerMessage message, String exceptThisAuthToken)
            throws IOException {
        System.out.println("Broadcasting message: " + message.toString());
        sessions.getSessionsForGame(gameID).forEach((authToken, session) -> {
            if (!Objects.equals(authToken, exceptThisAuthToken)) {
                try {
                    String newJsonMessage = new Gson().toJson(message);
                    System.out.println("Broadcasting message: " + newJsonMessage);
                    session.getRemote().sendString(newJsonMessage);
                } catch (IOException e) {
                    System.out.println("Error broadcasting message: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
    //changed
    public void joinPlayer(JoinUserCommand command, Session session) {
        // Get the joined game data
        GameInfo gameData = null;
        var username = command.getUsername();
        try {
            gameData = service.getGameData(command.getGameID(), command.getAuthString());
        } catch (DataAccessException e) {
            onError(session, e);
            return;
        }

        if (command.getUsername() == null) {
            setUsernameFromAuthToken(command, session);
        }

        // Get the game data for notifications
        var game = gameData.game();

        var whiteUsername = gameData.whiteUsername();
        var blackUsername = gameData.blackUsername();

        if (game.getTeamTurn() == TeamColor.FINISHED) {
            onError(session, new ResponseException(400, "Game is finished"));
            return;
        }
        if(gameData.whiteUsername() == null && gameData.blackUsername() == null) {
            onError(session, new ResponseException(400, "Game has not been created."));
            return;
        }
        if ((command.getPlayerColor() == TeamColor.WHITE &&  !gameData.whiteUsername().equals(command.getUsername())) || (command.getPlayerColor() == TeamColor.BLACK && !Objects.equals(gameData.blackUsername(), command.getUsername()))){
            onError(session, new ResponseException(400, "Username already taken"));
            return;
        }

        // Add the session to the game
        sessions.addSessionToGame(command.getGameID(), command.getAuthString(), session);

        sendMessagesForJoinAndObserve(game, command.getGameID(), command.getAuthString(), session, username + " has joined the game as the " + command.getPlayerColor().toString() + " player", whiteUsername, blackUsername);
    }




}
