package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class GameDAOTest {
    static GameDAO gameDAO = new SQLGameDao();

    @BeforeEach
    void reset() throws SQLException {
        gameDAO.clear();
    }

    @Test
    void clear() throws SQLException {
        GameData gameData = new GameData(1, null, null, "gamerMoment", new ChessGame());
        GameData gameData2 = new GameData(2, null, null, "gamerMoment2", new ChessGame());
        gameDAO.create(gameData);
        gameDAO.create(gameData2);
        gameDAO.clear();
        assertEquals(0, gameDAO.findAll().size());
    }

    @Test
    void create() throws SQLException {
        GameData gameData = new GameData(1, null, null, "gamerMoment", new ChessGame());
        gameDAO.create(gameData);
        assertEquals(1, gameDAO.findAll().size());
    }

    @Test
    void findAll() throws SQLException {
        GameData gameData = new GameData(1, null, null, "gamerMoment", new ChessGame());
        GameData gameData2 = new GameData(2, null, null, "gamerMoment2", new ChessGame());
        gameDAO.create(gameData);
        gameDAO.create(gameData2);
        assertEquals(2, gameDAO.findAll().size());
    }

    @Test
    void find() throws SQLException {
        GameData gameData = new GameData(1, null, null, "gamerMoment", new ChessGame());
        gameDAO.create(gameData);
        GameData result = gameDAO.find("1");
        assertEquals(gameData.gameName(), result.gameName());
    }

    @Test
    void remove() throws SQLException{
        GameData gameData = new GameData(1, null, null, "gamerMoment", new ChessGame());
        gameDAO.create(gameData);
        gameDAO.remove("1");
        assertEquals(0, gameDAO.findAll().size());
    }


    @Test
    void createFail() throws SQLException {
        boolean fail = false;
        GameData gameData = new GameData(1, null, null, null, new ChessGame());
        try {
            gameDAO.create(gameData);
        } catch (SQLException e) {
            fail = true;
        }
        assertTrue(fail);
    }

    @Test
    void findAllFail() throws SQLException {
        GameData gameData = new GameData(1, null, null, "gamerMoment", new ChessGame());
        GameData gameData2 = new GameData(2, null, null, "gamerMoment2", new ChessGame());
        gameDAO.create(gameData);
        gameDAO.create(gameData2);
        assertNotEquals(1, gameDAO.findAll().size());
    }

    @Test
    void findFail() throws SQLException {
        GameData gameData = new GameData(1, null, null, "gamerMoment", new ChessGame());
        gameDAO.create(gameData);
        GameData result = gameDAO.find("69");
        assertNull(result);
    }

    @Test
    void removeFail() throws SQLException{
        GameData gameData = new GameData(1, null, null, "gamerMoment", new ChessGame());
        gameDAO.create(gameData);
        gameDAO.remove("69");
        assertEquals(1, gameDAO.findAll().size());
    }
}