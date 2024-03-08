package dataAccess;
import model.*;

import java.util.ArrayList;

public interface GameDAO {
    void deleteEveryGame()throws DataAccessException;
    void createGame(GameInfo game)throws DataAccessException;
    ArrayList<GameInfo> readAllGames()throws DataAccessException;
    GameInfo readGame(Integer gameID)throws DataAccessException;
    void deleteGame(Integer gameID)throws DataAccessException;



}
