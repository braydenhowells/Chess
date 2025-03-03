package dataaccess;


import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MemoryGameDao implements GameDAO {

    private final static HashMap<String, GameData> allGames = new HashMap<>();

    public void clear() {
        allGames.clear();
    }

    public void create(GameData data) {
        allGames.put(String.valueOf(data.gameID()), data);
    }

    public List<GameData> findAll() {
        return new ArrayList<>(allGames.values());
    }

    public GameData find(String gameID) {
        return allGames.get(gameID); // return null if not found
    }

    public void remove(String gameID) {
        allGames.remove(gameID);
    }
}
