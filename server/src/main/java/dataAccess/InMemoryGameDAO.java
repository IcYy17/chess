package dataAccess;

import model.Game;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryGameDAO implements IGameDAO {
    private Map<Integer, Game> games = new HashMap<>();


}