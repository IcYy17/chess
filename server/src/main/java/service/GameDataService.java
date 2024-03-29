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

    public void joinGame(JoinGameRequest request, String username) throws DataAccessException {
        GameInfo game = gameDataDAO.readGame(request.gameId());
        if (game == null) {
            throw new DataAccessException("Error: bad request");
        }

        if (("BLACK".equals(request.playerColor()) && game.blackUsername() != null) ||
                ("WHITE".equals(request.playerColor()) && game.whiteUsername() != null)) {
            throw new DataAccessException("Error: already taken");
        }

        String updatedBlackUsername = "BLACK".equals(request.playerColor()) ? username : game.blackUsername();
        String updatedWhiteUsername = "WHITE".equals(request.playerColor()) ? username : game.whiteUsername();
        GameInfo updatedGame = new GameInfo(request.gameId(), updatedWhiteUsername, updatedBlackUsername, game.gameName(), game.game());

        gameDataDAO.deleteGame(request.gameId());
        gameDataDAO.createGame(updatedGame);
    }
}
