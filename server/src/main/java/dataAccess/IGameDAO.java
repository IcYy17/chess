package dataAccess;

import model.Game;

import java.util.List;

public interface MyGameDAO extends MyDataAccess {
    void createGame(Game game) throws DataAccessException;
    Game getGame(int gameID) throws DataAccessException;
    List<Game> listGames() throws DataAccessException;
    void updateGame(Game game) throws DataAccessException;
}
