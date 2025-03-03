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

    public GameData findGame(String gameID) {
        return gameDao.find(gameID);
    }

    public void updateGameUser(String username, GameData gameData, String color) {
        // thought process is delete the old one, and keep the new one but set the new username
        if (color.equals("BLACK")) {
            gameDao.remove(String.valueOf(gameData.gameID()));
            gameDao.create(new GameData(gameData.gameID(), gameData.whiteUsername(), username, gameData.gameName(), gameData.game()));
        }

        if (color.equals("WHITE")) {
            gameDao.remove(String.valueOf(gameData.gameID()));
            gameDao.create(new GameData(gameData.gameID(), username, gameData.blackUsername(), gameData.gameName(), gameData.game()));
        }
    }


}
