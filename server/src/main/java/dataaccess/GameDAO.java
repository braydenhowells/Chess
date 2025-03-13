package dataaccess;

import model.GameData;

import java.sql.SQLException;
import java.util.List;

public interface GameDAO {
    void clear() throws SQLException;
    int create(GameData data) throws SQLException;
    List<GameData> findAll() throws SQLException;
    GameData find(String gameID) throws SQLException;
    void remove(String gameID) throws SQLException;
}
