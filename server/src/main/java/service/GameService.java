package service;

import dataaccess.GameDAO;
import dataaccess.MemoryGameDao;

public class GameService {
    private final GameDAO gameDao;

    public GameService(GameDAO gameDao) {
        this.gameDao = gameDao;
    }

    public void clear() {
        gameDao.clear();
    }
}
