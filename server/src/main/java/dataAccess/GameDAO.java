package dataAccess;
import model.*;

import java.util.ArrayList;

public interface GameDAO {
    void deleteAllGames();
    void createGame(GameInfo game);
    GameInfo readGame(Integer gameID);
    void deleteGame(Integer gameID);
    ArrayList<GameInfo> readAllGames();


}
