package dataAccess;

import model.GameInfo;

import java.util.ArrayList;
import java.util.HashMap;
public class MemoryGameDAO implements GameDAO{
    private final HashMap<Integer, GameInfo> gameDataMap = new HashMap<>();

    public void createGame(GameInfo game){
        gameDataMap.put(game.gameID(), game);
    }

    public GameInfo readGame(Integer gameID){
        return gameDataMap.get(gameID);
    }

    public void deleteGame(Integer gameID){
        gameDataMap.remove(gameID);
    }

    public ArrayList<GameInfo> readAllGames(){
        return new ArrayList<>(gameDataMap.values());
    }
    public void deleteAllGames(){
        gameDataMap.clear();
    }
}