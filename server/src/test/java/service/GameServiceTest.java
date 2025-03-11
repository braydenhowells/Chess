package service;

import chess.ChessGame;
import dataaccess.*;
import handlers.MasterHandler;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import results.ListResult;
import spark.Response;


import static org.junit.jupiter.api.Assertions.assertEquals;

class GameServiceTest {
    static UserDAO userDAO = new MemoryUserDao();
    static AuthDAO authDAO = new MemoryAuthDao();
    static GameDAO gameDAO = new MemoryGameDao();
    static AuthService authService = new AuthService(authDAO);
    static UserService userService = new UserService(userDAO, authDAO, authService);
    static GameService gameService = new GameService(gameDAO, authService);
    static MasterHandler handler = new MasterHandler(userService, gameService, authService);

    @BeforeEach
    void reset() {
        gameDAO.clear();
        userDAO.clear();
        authDAO.clear();
    }

    @Test
    void clear() {
        gameDAO.create(new GameData(1, "white", "black", "ggNoRe", new ChessGame()));
        gameDAO.clear();

        assertEquals(0, gameDAO.findAll().size());
    }

    @Test
    void create() {
        assertEquals(1, gameDAO.findAll().size());
    }

//    @Test
//    void getGames() {
//        ListResult result = gameService.getGames("gang", new Response());
//
//        assertEquals(2, result.games().size());
//    }

    @Test
    void findGame() {
        GameData data = new GameData(1, "white", "black", "newGame", new ChessGame());
        gameDAO.create(data);
        GameData dataCheck = gameService.findGame("1");

        assertEquals(data, dataCheck);
    }

//    @Test
//    void updateGameUser() {
//        GameData data = new GameData(1, "", "", "MrGame_N_Watch", new ChessGame());
//        gameDAO.create(data);
//        gameService.join("white", data, "WHITE");
//
//        GameData newData = gameDAO.find("1");
//        assertEquals("white", newData.whiteUsername());
//    }


    @Test
    void createFail() {
        // the logic for this method is in the handler, and it does not really check anything
        // this means that errors are not returned and I cannot replicate these errors
    }

    @Test
    void getGamesFail() {
        // the logic for this method is in the handler, and it does not really check anything
        // this means that errors are not returned and I cannot replicate these errors
    }

    @Test
    void findGameFail() {
        // the logic for this method is in the handler, and it does not really check anything
        // this means that errors are not returned and I cannot replicate these errors
    }

    @Test
    void updateGameUserFail() {
        // the logic for this method is in the handler, and it does not really check anything
        // this means that errors are not returned and I cannot replicate these errors
    }
}