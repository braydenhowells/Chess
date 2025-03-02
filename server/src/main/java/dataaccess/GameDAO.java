package dataaccess;

import model.GameData;

import java.util.List;

public interface GameDAO {
    void clear();
    void create(GameData data);
    List<GameData> findAll();
}
