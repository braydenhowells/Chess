package dataaccess;


import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MemoryGameDao implements GameDAO {

    private final static HashMap<String, GameData> ALL_GAMES = new HashMap<>();

    public void clear() {
        ALL_GAMES.clear();
    }

    public int create(GameData data) {
        ALL_GAMES.put(String.valueOf(data.gameID()), data);
        return data.gameID();
    }

    public List<GameData> findAll() {
        return new ArrayList<>(ALL_GAMES.values());
    }

    public GameData find(String gameID) {
        return ALL_GAMES.get(gameID); // return null if not found
    }

    public void remove(String gameID) {
        ALL_GAMES.remove(gameID);
    }
}
