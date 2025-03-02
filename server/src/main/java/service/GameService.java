package service;

import chess.ChessGame;
import dataaccess.GameDAO;
import model.GameData;
import results.CreateResult;

public class GameService {
    private final GameDAO gameDao;
    private int gameIDcounter = 0;

    public GameService(GameDAO gameDao) {
        this.gameDao = gameDao;

    }

    public void clear() {
        gameDao.clear();
    }

    public CreateResult create(String gameName) {
        GameData data = new GameData(this.gameIDcounter, null, null, gameName, new ChessGame());
        gameDao.create(data);
        this.gameIDcounter += 1;
        // placeholder
        return new CreateResult(null, null);
    }
}
