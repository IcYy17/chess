package service;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.MemoryGameDAO;
import model.GameInfo;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
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
    public CreateGameResponse createGame(CreateGameRequest requ) throws DataAccessException {
        ChessGame game = new ChessGame();
        if(requ.gameName() == null){
            throw new DataAccessException("Error: bad request");
        }
        Random num = new Random();
        int gameNum = Math.abs(num.nextInt());
        GameInfo gameInfo = new GameInfo(gameNum,null,null, requ.gameName(),game);
        gameDataDAO.createGame(gameInfo);
        return new CreateGameResponse(gameNum);
    }

    public void joinGame(JoinGameRequest requ, String username) throws DataAccessException {
        if (gameDataDAO.readGame(requ.gameID()) == null) {
            throw new DataAccessException("Error: bad request");
        }
        if(requ.playerColor() != null){
            Integer gameID = requ.gameID();
            GameInfo lastGame = gameDataDAO.readGame(gameID);
            if(requ.playerColor().equals("BLACK") && lastGame.blackUsername() != null){
                throw new DataAccessException("Error: already taken");
            }
            if(requ.playerColor().equals("WHITE") && lastGame.whiteUsername() != null){
                throw new DataAccessException("Error: already taken");
            }
            String color = requ.playerColor();
            String blackUser = color.equals("BLACK") ? username : lastGame.blackUsername();
            String whiteUser = color.equals("WHITE") ? username : lastGame.whiteUsername();
            String gameNum = lastGame.gameName();
            ChessGame game = lastGame.game();
            GameInfo newGame = new GameInfo(gameID,whiteUser,blackUser,gameNum,game);
            gameDataDAO.deleteGame(gameID);
            gameDataDAO.createGame(newGame);
        }
    }
}
