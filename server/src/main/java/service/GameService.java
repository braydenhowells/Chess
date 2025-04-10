package service;

import chess.ChessGame;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import requests.JoinRequest;
import results.CreateResult;
import results.ListResult;
import results.SimpleResult;

import java.sql.SQLException;
import java.util.Arrays;
// using Arrays.asList here allows null values to enter the list when we use the verifyAuth function
// this is crucial because any null values in our body parts need to be accounted for
// also, if we use a regular list instead, java will flip out if anything inside is null

public class GameService {
    private final GameDAO gameDao;
    private final AuthService authService;

    public GameService(GameDAO gameDao, AuthService authService) {
        this.gameDao = gameDao;
        this.authService = authService;
    }

    public SimpleResult clear() {
        try {
            gameDao.clear();
            // return simple result w null message
            return new SimpleResult(null);
        }
        catch (SQLException e) {
            // return simple result with e.get message as the message
            return new SimpleResult(e.getMessage());
        }


    }

    public CreateResult create(String gameName, String authToken) {
        String verification = authService.verifyAuth(authToken,true, Arrays.asList(gameName));
        if (verification.contains("Error")) {
            return new CreateResult(null, verification);
        }

        try {
            GameData data = new GameData(0, null, null, gameName, new ChessGame(), false);
            int gameID = gameDao.create(data);
            // return simple result w null message
            return new CreateResult(String.valueOf(gameID), null);
        }
        catch (SQLException e) {
            // return simple result with e.get message as the message
            return new CreateResult(null, e.getMessage());
        }
    }

    public ListResult getGames(String authToken) {
        String verification = authService.verifyAuth(authToken,false, null);
        if (verification.contains("Error")) {
            return new ListResult(verification, null);
        }

        try {
            return new ListResult(null, gameDao.findAll());
        } catch (SQLException e) {
            return new ListResult(e.getMessage(), null);
        }

    }

    public GameData findGame(String gameID) {
        try {
            return gameDao.find(gameID);
        }
        catch (SQLException e) {
            return null;
        }
    }

    public void updateGame(GameData updatedGame) throws SQLException {
        gameDao.remove(String.valueOf(updatedGame.gameID()));
        gameDao.create(updatedGame);
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
                if (data.blackUsername() != null && !username.equals(data.blackUsername())) {
                    // the second check allows us to reuse join for leaving purposes ^
                    return new SimpleResult("Error: already taken"); // 403 error
                }
            }

            if (jreq.playerColor().equals("WHITE")) {
                if (data.whiteUsername() != null && !username.equals(data.whiteUsername())) {
                    // the second check allows us to reuse join for leaving purposes ^
                    return new SimpleResult("Error: already taken"); // 403 error
                }
            }
        }


        try {

            // thought process is delete the old one, and keep the new one but set the new username
            // this allows for joining or LEAVING from a user who is already in the game
            if (color.equals("BLACK")) {
                String newBlack;
                if (username.equals(gameData.blackUsername())) {
                    newBlack = null;
                } else {
                    newBlack = username;
                }

                gameDao.remove(String.valueOf(gameData.gameID()));
                gameDao.create(new GameData(gameData.gameID(), gameData.whiteUsername(), newBlack,
                        gameData.gameName(), gameData.game(), gameData.gameOver()));
            }

            if (color.equals("WHITE")) {
                String newWhite;
                if (username.equals(gameData.whiteUsername())) {
                    newWhite = null;
                } else {
                    newWhite = username;
                }

                gameDao.remove(String.valueOf(gameData.gameID()));
                gameDao.create(new GameData(gameData.gameID(), newWhite, gameData.blackUsername(),
                        gameData.gameName(), gameData.game(), gameData.gameOver()));
            }


            // done, so return an empty message
            return new SimpleResult(null);

        }
        catch (SQLException e) {
            // return simple result with e.get message as the message
            return new SimpleResult(e.getMessage());
        }

    }
}
