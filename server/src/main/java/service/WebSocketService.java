package service;

import chess.ChessMove;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.WebSocketAccess;
import exception.ResponseException;
import model.GameInfo;

public class WebSocketService {
    private WebSocketAccess data = new WebSocketAccess();

    public GameInfo getGameData(int gameID, String authToken) throws DataAccessException {
        if (gameID <= 0 || authToken == null || authToken.isEmpty()){
            throw new DataAccessException("Error: bad request");
        }
        return data.getGameData(gameID, authToken);
    }

    public String getUsernameFromAuthToken(String authToken) throws DataAccessException {
        if (authToken == null || authToken.isEmpty()){
            throw new DataAccessException("Error: bad request");
        }
        return data.getUsernameFromAuthToken(authToken);
    }

    public GameInfo makeMove(int gameID, String authToken, ChessMove move) throws DataAccessException {
        if (gameID <= 0 || authToken == null || authToken.isEmpty() || move == null){
            throw new DataAccessException("Error: bad request");
        }
        return data.makeMove(gameID, authToken, move);
    }

    public String leaveGame(int gameID, String authToken) throws DataAccessException {
        if (gameID <= 0 || authToken == null || authToken.isEmpty()){
            throw new DataAccessException("Error: bad request");
        }
        return data.leaveGame(gameID, authToken);
    }

    public String resignGame(int gameID, String authToken) throws DataAccessException {
        if (gameID <= 0 || authToken == null || authToken.isEmpty()){
            throw new DataAccessException("Error: bad request");
        }
        return data.resignGame(gameID, authToken);
    }




}
