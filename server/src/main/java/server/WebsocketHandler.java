package server;

import chess.ChessGame;
import com.google.gson.Gson;

import chess.ChessGame.TeamColor;
import exception.ResponseException;
import model.GameInfo;

import org.eclipse.jetty.websocket.api.*;

import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import webSocketMessages.serverMessages.*; //need to add more to both server and user
import webSocketMessages.userCommands.*;

import webSocketMessages.userCommands.UserGameCommand;
import webSocketMessages.serverMessages.ServerMessage;
import service.*;
import java.io.IOException;
import java.util.Objects;

public class WebsocketHandler {
    private WebsocketSession sessions = new WebsocketSession();;
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


}
