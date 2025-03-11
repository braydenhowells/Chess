package service;

import chess.ChessGame;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import requests.JoinRequest;
import results.CreateResult;
import results.ListResult;
import results.SimpleResult;

import java.util.Arrays;
// using Arrays.asList here allows null values to enter the list when we use the verifyAuth function
// this is crucial because any null values in our body parts need to be accounted for
// also, if we use a regular list instead, java will flip out if anything inside is null

public class GameService {
    private final GameDAO gameDao;
    private int gameIDcounter = 1;
    private final AuthService authService;

    public GameService(GameDAO gameDao, AuthService authService) {
        this.gameDao = gameDao;
        this.authService = authService;
    }

    public void clear() {
        gameDao.clear();
        gameIDcounter = 1;
    }

    public CreateResult create(String gameName, String authToken) {
        String verification = authService.verifyAuth(authToken,true, Arrays.asList(gameName));
        if (verification.contains("Error")) {
            return new CreateResult(null, verification);
        }
        GameData data = new GameData(this.gameIDcounter, null, null, gameName, new ChessGame());
        gameDao.create(data);
        this.gameIDcounter += 1;
        return new CreateResult(String.valueOf(data.gameID()), null);
    }

    public ListResult getGames(String authToken) {
        String verification = authService.verifyAuth(authToken,false, null);
        if (verification.contains("Error")) {
            return new ListResult(verification, null);
        }
        return new ListResult(null, gameDao.findAll());
    }

    public GameData findGame(String gameID) {
        return gameDao.find(gameID);
    }

    public SimpleResult join(GameData gameData, String color, String authToken, JoinRequest jreq) {
        String verification = authService.verifyAuth(authToken,true, Arrays.asList(jreq.gameID(), jreq.playerColor()));

        // error cases: 400
        if (verification.contains("Error")) {
            return new SimpleResult(verification);
        }

        // now we know that the auth is legit and the body parts aren't null
        AuthData authData = authService.getAuthData(authToken);
        String username = authData.username();

        if (verification.contains("verified")) {
            // first, make sure that the game exists by gameID
            GameData data = findGame(jreq.gameID());

            // case where supplied ID does not find a game, 401
            if (data == null) {
                // 401 error
                return new SimpleResult("Error: bad request");
            }

            // case where game color is not black / white, 400
            if (!jreq.playerColor().equals("BLACK") && (!jreq.playerColor().equals("WHITE"))) {
                // 400 error
                return new SimpleResult("Error: bad request");
            }

            // case where the desired color is already taken, 403
            if (jreq.playerColor().equals("BLACK")) {
                if (data.blackUsername() != null) {
                    // 403 error
                    return new SimpleResult("Error: already taken");
                }
            }

            if (jreq.playerColor().equals("WHITE")) {
                if (data.whiteUsername() != null) {
                    // 403 error
                    return new SimpleResult("Error: already taken");
                }
            }
        }


        // thought process is delete the old one, and keep the new one but set the new username
        if (color.equals("BLACK")) {
            gameDao.remove(String.valueOf(gameData.gameID()));
            gameDao.create(new GameData(gameData.gameID(), gameData.whiteUsername(), username, gameData.gameName(), gameData.game()));
        }

        if (color.equals("WHITE")) {
            gameDao.remove(String.valueOf(gameData.gameID()));
            gameDao.create(new GameData(gameData.gameID(), username, gameData.blackUsername(), gameData.gameName(), gameData.game()));
        }

        // done, so return an empty message
        return new SimpleResult(null);

    }
}
