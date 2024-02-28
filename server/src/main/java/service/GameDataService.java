package service;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.MemoryGameDAO;
import model.GameInfo;
import requests.CreateGameRequest;
import response.CreateGameResponse;

import java.util.Random;

public class GameDataService {
    private final MemoryGameDAO gameDAO = new MemoryGameDAO();

//    public void clear(){
//        gameDAO.deleteAllGames();
//    }
    public CreateGameResponse createGame(CreateGameRequest request) throws DataAccessException {
        if(request.gameName() == null){
            throw new DataAccessException("bad request");
        }
        Random random = new Random();
        Integer gameID = Math.abs(random.nextInt());
        ChessGame game = new ChessGame();
        GameInfo gameData = new GameInfo(gameID,null,null, request.gameName(),game);
        gameDAO.createGame(gameData);
        return new CreateGameResponse(gameID);
    }
}
