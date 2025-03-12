package dataaccess;

import model.GameData;

import java.sql.SQLException;
import java.util.List;

public interface GameDAO {
    void clear() throws SQLException;
    void create(GameData data);
    List<GameData> findAll();
    GameData find(String gameID);
    void remove(String gameID);
}
