package service;

import dataaccess.GameDAO;
import dataaccess.MemoryGameDao;

public class GameService {
    private final GameDAO gameDao = new MemoryGameDao();

    public void clear() {
        gameDao.clear();
    }
}
