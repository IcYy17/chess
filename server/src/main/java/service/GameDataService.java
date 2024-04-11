package service;

import chess.ChessGame;
import dataAccess.DataAccessException;
import model.GameInfo;
import mySQLdata.GameSQL;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import response.CreateGameResponse;
import response.ListGamesResponse;

import java.util.Random;

public class GameDataService {
    private final GameSQL gameDataDAO = new GameSQL();

    public void clear()throws DataAccessException{
        gameDataDAO.deleteAllGames();
    }
    public ListGamesResponse listGames()throws DataAccessException{
        return new ListGamesResponse(gameDataDAO.readAllGames());
    }
    public CreateGameResponse createGame(CreateGameRequest request) throws DataAccessException {
        if (request.gameName() == null || request.gameName().isEmpty()) {
            throw new DataAccessException("Error: bad request");
        }
        int gameId = new Random().nextInt(Integer.MAX_VALUE);
        ChessGame newGame = new ChessGame(); // Assuming ChessGame initialization can be done here directly.
        GameInfo newGameInfo = new GameInfo(gameId, null, null, request.gameName(), newGame);
        gameDataDAO.createGame(newGameInfo);
        return new CreateGameResponse(gameId, newGameInfo.whiteUsername(), newGameInfo.blackUsername(), newGameInfo.gameName(), newGameInfo.game());
    }


    //changed gameId to gameID in this and joinGameRequest
    public void joinGame(JoinGameRequest request, String username) throws DataAccessException {
        GameInfo game = gameDataDAO.readGame(request.gameID());
        if (game == null) {
            throw new DataAccessException("Error: bad request");
        }
        if (request.playerColor() !=null ) {
            String playerColor = request.playerColor().toUpperCase();
            if (("BLACK".equals(playerColor) && game.blackUsername() != null) ||
                    ("WHITE".equals(playerColor) && game.whiteUsername() != null)) {
                throw new DataAccessException("Error: already taken");
            }

            String updatedBlackUsername = "BLACK".equals(playerColor) ? username : game.blackUsername();
            String updatedWhiteUsername = "WHITE".equals(playerColor) ? username : game.whiteUsername();
            GameInfo updatedGame = new GameInfo(request.gameID(), updatedWhiteUsername, updatedBlackUsername, game.gameName(), game.game());

            gameDataDAO.deleteGame(request.gameID());
            gameDataDAO.createGame(updatedGame);
        }
    }
}
