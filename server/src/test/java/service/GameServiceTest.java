package service;

import chess.ChessGame;
import dataaccess.*;
import handlers.MasterHandler;
import model.AuthData;
import model.GameData;
import model.UserData;
import requests.JoinRequest;
import results.ListResult;
import results.LoginResult;
import requests.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    static UserDAO userDAO = new SQLUserDao();
    static AuthDAO authDAO = new SQLAuthDao();
    static GameDAO gameDAO = new SQLGameDao();
    static AuthService authService = new AuthService(authDAO);
    static UserService userService = new UserService(userDAO, authDAO, authService);
    static GameService gameService = new GameService(gameDAO, authService);
    static MasterHandler handler = new MasterHandler(userService, gameService, authService);

    @BeforeEach
    void reset() throws SQLException {
        gameDAO.clear();
        userDAO.clear();
        authDAO.clear();
    }

    @Test
    void clear() throws SQLException {
        gameDAO.create(new GameData(1, "white", "black", "ggNoRe", new ChessGame()));
        gameDAO.clear();
        assertEquals(0, gameDAO.findAll().size());
    }

    @Test
    void create() throws SQLException {
        assertEquals(0, gameDAO.findAll().size());
        gameDAO.create(new GameData(1, "white", "black", "newGame", new ChessGame()));
        assertEquals(1, gameDAO.findAll().size());
    }

    @Test
    void getGames() throws SQLException {
        gameDAO.create(new GameData(1, "white", "black", "Game1", new ChessGame()));
        gameDAO.create(new GameData(2, "white2", "black2", "Game2", new ChessGame()));
        ListResult result = gameService.getGames("authToken");
        assertNotNull(result);
        assertEquals(2, result.games().size());
    }

    @Test
    void findGame() throws SQLException {
        GameData data = new GameData(1, "white", "black", "newGame", new ChessGame());
        gameDAO.create(data);
        GameData dataCheck = gameService.findGame("1");
        assertNotNull(dataCheck);
        assertEquals(data, dataCheck);
    }

    @Test
    void updateGameUser() throws SQLException {
        GameData gameData = new GameData(1, null, null, "MrGame_N_Watch", new ChessGame());
        AuthData authData = new AuthData("epicToken", "CaptainFalcon");
        gameDAO.create(gameData);
        authDAO.createAuth(authData);
        gameService.join(gameData, "WHITE", "epicToken", new JoinRequest("WHITE", "1"));
        GameData newData = gameDAO.find("1");
        assertEquals("CaptainFalcon", newData.whiteUsername());
    }

    @Test
    void createFail() {
        assertThrows(SQLException.class, () -> gameDAO.create(null));
    }

    @Test
    void getGamesFail() {
        assertThrows(SQLException.class, () -> gameService.getGames(null));
    }

    @Test
    void findGameFail() {
        assertNull(gameService.findGame("99999"));
    }

    @Test
    void updateGameUserFail() throws SQLException {
        GameData gameData = new GameData(1, null, null, "MrGame_N_Watch", new ChessGame());
        UserData userData = new UserData("CaptainFalcon", "yuh", "yuh");
        AuthData authData = new AuthData("sadToken", "CaptainFalcon");
        gameDAO.create(gameData);
        authDAO.createAuth(authData);
        gameService.join(gameData, "WHITE", "epicToken", new JoinRequest("WHITE", "1"));
        GameData newData = gameDAO.find("1");
        assertEquals("CaptainFalcon", newData.whiteUsername());
    }
}
