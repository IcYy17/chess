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

    @Override
    public void onOpen(Session session, EndpointConfig config) {
    }

    @OnClose
    public void onClose(){
    }

    @OnError
    public void onError(){
    }






}
