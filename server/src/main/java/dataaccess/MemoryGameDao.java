package dataaccess;

import model.AuthData;
import model.GameData;

import java.util.HashMap;

public class MemoryGameDao implements GameDAO {

    private final static HashMap<String, GameData> allGames = new HashMap<>();


    public void clear() {
        allGames.clear();
    }
}
