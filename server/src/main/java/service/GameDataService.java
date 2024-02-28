package service;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.MemoryGameDAO;
import model.GameInfo;
import requests.CreateGameRequest;
import response.CreateGameResponse;
import response.ListGamesResponse;

import java.util.Random;

public class GameDataService {
    private final MemoryGameDAO gameDataDAO = new MemoryGameDAO();

    public void clear(){
        gameDataDAO.deleteAllGames();
    }
    public ListGamesResponse listGames(){
        return new ListGamesResponse(gameDataDAO.readAllGames());
    }
    public CreateGameResponse createGame(CreateGameRequest req) throws DataAccessException {
        ChessGame game = new ChessGame();
        if(req.gameName() == null){
            throw new DataAccessException("bad request");
        }
        Random num = new Random();
        Integer gameID = Math.abs(num.nextInt());
        GameInfo gameData = new GameInfo(gameID,null,null, req.gameName(),game);
        gameDataDAO.createGame(gameData);
        return new CreateGameResponse(gameID);
    }
}
