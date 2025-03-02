package service;

import chess.ChessGame;
import dataaccess.GameDAO;
import model.GameData;
import results.CreateResult;
import results.ListResult;

import java.util.List;

public class GameService {
    private final GameDAO gameDao;
    private int gameIDcounter = 1;

    public GameService(GameDAO gameDao) {
        this.gameDao = gameDao;
    }

    public void clear() {
        gameDao.clear();
        gameIDcounter = 1;
    }

    public CreateResult create(String gameName) {
        GameData data = new GameData(this.gameIDcounter, null, null, gameName, new ChessGame());
        gameDao.create(data);
        this.gameIDcounter += 1;
        return new CreateResult(String.valueOf(data.gameID()), null);
    }

    public ListResult getGames() {
        return new ListResult(null, gameDao.findAll());
    }
}
