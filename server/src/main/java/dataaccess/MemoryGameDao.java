package dataaccess;


import model.GameData;

import java.util.HashMap;

public class MemoryGameDao implements GameDAO {

    private final static HashMap<String, GameData> allGames = new HashMap<>();


    public void clear() {
        allGames.clear();
    }

    public void create(GameData data) {
        allGames.put(String.valueOf(data.gameID()), data);
    }
}
