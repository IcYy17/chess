package dataAccess;
import model.*;

public interface GameDAO {
    void deleteAllGames();
    void createGame(GameInfo game);
    GameInfo readGame(Integer gameID);
    void deleteGame(Integer gameID);



}
