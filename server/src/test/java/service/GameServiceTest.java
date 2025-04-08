package service;

import chess.ChessGame;
import dataaccess.*;
import handlers.MasterHandler;
import model.AuthData;
import model.GameData;
import model.UserData;
import requests.JoinRequest;
import results.CreateResult;
import results.ListResult;
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

    @BeforeEach
    void reset() throws SQLException {
        gameDAO.clear();
        userDAO.clear();
        authDAO.clear();
    }

    @Test
    void clear() throws SQLException {
        gameDAO.create(new GameData(1, null, null, "gamer", new ChessGame(), false));
        gameService.clear();
        assertEquals(0, gameDAO.findAll().size());
    }

    @Test
    void create() throws SQLException {
        assertEquals(0, gameDAO.findAll().size());
        authDAO.createAuth(new AuthData("epicToken", "me"));
        gameService.create("gamer", "epicToken");
        assertEquals(1, gameDAO.findAll().size());
    }

    @Test
    void getGames() throws SQLException {
        gameDAO.create(new GameData(1, null, null, "gamer", new ChessGame(), false));
        gameDAO.create(new GameData(2, "white_prefilled_i_guess", "black_is_here_also", "gamer2electricBoogaloo", new ChessGame(), false));
        authDAO.createAuth(new AuthData("yuh", "yuh"));
        ListResult result = gameService.getGames("yuh");
        assertNotNull(result);
        assertEquals(2, result.games().size());
    }

    @Test
    void findGame() throws SQLException {
        GameData gameData = new GameData(1, null, null, "gamer", new ChessGame(), false);
        gameDAO.create(gameData);
        GameData result = gameService.findGame("1");
        assertNotNull(result);
        assertEquals(gameData.gameID(), result.gameID());
    }

    @Test
    void updateGameUser() throws SQLException {
        GameData gameData = new GameData(1, null, null, "MrGame_N_Watch", new ChessGame(), false);
        AuthData authData = new AuthData("epicToken", "CaptainFalcon");
        gameDAO.create(gameData);
        authDAO.createAuth(authData);
        gameService.join(gameData, "WHITE", "epicToken", new JoinRequest("WHITE", "1"));
        GameData newData = gameDAO.find("1");
        assertEquals("CaptainFalcon", newData.whiteUsername());
    }

    @Test
    void createFail() {
        CreateResult result = gameService.create(null, null);
        assertTrue(result.message().contains("Error"));

    }

    @Test
    void getGamesFail() {
        ListResult result = gameService.getGames(null);
        assertTrue(result.message().contains("Error"));
    }

    @Test
    void findGameFail() {
        assertNull(gameService.findGame(null));
    }

    @Test
    void updateGameUserFail() throws SQLException {
        GameData gameData = new GameData(1, null, null, "MrGame_N_Watch", new ChessGame(), false);
        AuthData authData = new AuthData("sadToken", "CaptainFalcon");
        gameDAO.create(gameData);
        authDAO.createAuth(authData);
        gameService.join(gameData, "WHITE", "epicToken", new JoinRequest("WHITE", "1"));
        GameData newData = gameDAO.find("1");
        assertNotEquals("CaptainFalcon", newData.whiteUsername());
    }
}
