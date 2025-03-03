package handlers;

import com.google.gson.Gson;
import model.GameData;
import requests.*;
import results.CreateResult;
import results.ListResult;
import results.LoginResult;
import results.SimpleResult;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;
import model.AuthData;

import java.util.Arrays;
import java.util.List;
// using Arrays.asList here allows null values to enter the list when we use the verifyAuth function
// this is crucial because any null values in our body parts need to be accounted for
// also, if we use a regular list instead, java will flip out if anything inside is null

public class MasterHandler {

    private final UserService userService;
    private final GameService gameService;

    public MasterHandler(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
    }

    public String verifyAuth(String authToken, Response res, boolean checkBody, List<String> bodyParts) {
        // check for empty token, 400
        if (authToken == null || authToken.isEmpty()) {
            res.status(400);
            return "Error: bad request";
        }
        // check to see if token is wrong, 401
        AuthData data = userService.getAuthData(authToken);
        if (data == null) {
            res.status(401);
            return "Error: unauthorized";
        }
        // see if any the api body pieces are null/empty, 400
        if (checkBody && bodyParts != null) { // check list != null just in case
            for (String bodyPart : bodyParts) {
                if (bodyPart == null || bodyPart.isEmpty()) {
                    res.status(400);
                    return "Error: bad request";
                }
            }
        }
        // if we made it this far, we are golden
        return "verified";
    }

    public Object clear(Response res) {
        // idk how a 500 error would even look for this one. not sure where to put it
        userService.authClear();
        userService.userClear();
        gameService.clear();
        res.status(200);
        return new Gson().toJson(null);
    }

    public Object create(Request req, Response res) {
        CreateRequest creq = new Gson().fromJson(req.body(), CreateRequest.class);
        // verify auth token
        String authToken = req.headers("authorization");
        String verification = verifyAuth(authToken, res, true, Arrays.asList(creq.gameName()));
        // error cases: 400, 401
        if (verification.contains("Error")) {
            return new Gson().toJson(new SimpleResult(verification));
        }
        // if verified, create game
        else if (verification.contains("verified")) {
            CreateResult result = gameService.create(creq.gameName());
            res.status(200);
            // return the result as a JSON
            return new Gson().toJson(result);
        }
        else {
            res.status(500);
            return new Gson().toJson(new SimpleResult("Error: an unexpected error occurred"));
        }
    }

    public Object list(String authToken, Response res) {
        // verify auth token
        String verification = verifyAuth(authToken, res, false, null);
        // bad path
        if (verification.contains("Error")) {
            return new Gson().toJson(new SimpleResult(verification));
        }
        // good path
        if (verification.contains("verified")) {
            // now we know that the token is legit. let's proceed
            ListResult games = gameService.getGames();
            res.status(200);
            // jsonify that sucker
            return new Gson().toJson(games);
        }
        // wacky path
        else {
            res.status(500);
            return new Gson().toJson(new SimpleResult("Error: an unexpected error occurred"));
        }
    }

    public Object logout(String authToken, Response res) {
        // verify auth token
        String verification = verifyAuth(authToken, res, false, null);
        // bad path
        if (verification.contains("Error")) {
            return new Gson().toJson(new SimpleResult(verification));
        }
        // good path
        if (verification.contains("verified")) {
            // now we know that the token is legit. let's proceed
            LogoutRequest lreq = new LogoutRequest(authToken);
            SimpleResult result = userService.logout(lreq);
            res.status(200);
            // jsonify that sucker
            return new Gson().toJson(result);
        }
        // wacky path
        else {
            res.status(500);
            return new Gson().toJson(new SimpleResult("Error: an unexpected error occurred"));
        }
    }

    public Object login(Request req, Response res) {
        LoginRequest lreq = new Gson().fromJson(req.body(), LoginRequest.class);

        // make a check for null values in rreq, possibly return 400 bad request, missing a piece of info
        if (lreq.username() == null || lreq.username().isEmpty() ||
                lreq.password() == null || lreq.password().isEmpty()) {

            res.status(400);
            return new Gson().toJson(new SimpleResult("Error: bad request"));
        }

        LoginResult result = userService.login(lreq);

        // 200
        if (result.message() == null) {
            res.status(200);
            System.out.println("200");
        }

        // 401 and 500
        if (result.message()!= null) {
            if (result.message().contains("unauthorized")) {
                res.status(401);
            }
            else {
                res.status(500);
            }
        }
        // return the login result when finished, as a JSON
        return new Gson().toJson(result);
    }

    public Object register(Request req, Response res){
        RegisterRequest rreq = new Gson().fromJson(req.body(), RegisterRequest.class);
        // make a check for null values in rreq, possibly return 400 bad request, missing a piece of info
        if (rreq.email() == null || rreq.email().isEmpty() ||
                rreq.username() == null || rreq.username().isEmpty() ||
                rreq.password() == null || rreq.password().isEmpty()) {

            res.status(400);
            return new Gson().toJson(new SimpleResult("Error: bad request"));
        }
        LoginResult result = userService.register(rreq);
        // 200
        if (result.message() == null) {
            res.status(200);
        }
        // 403
        if (result.message()!= null) {
            if (result.message().contains("taken")) {
                res.status(403);
            }
            else {
                res.status(500);
            }
        }
        // return the register result when finished, as a JSON
        return new Gson().toJson(result);
    }

    public Object join(Request req, Response res) {
        JoinRequest jreq = new Gson().fromJson(req.body(), JoinRequest.class);

        // verify auth token
        String authToken = req.headers("authorization");
        String verification = verifyAuth(authToken, res, true, Arrays.asList(jreq.gameID(), jreq.playerColor()));

        // error cases: 400, 401
        if (verification.contains("Error")) {
            return new Gson().toJson(new SimpleResult(verification));
        }

        // now we know that the auth is legit and the body parts aren't null
        else if (verification.contains("verified")) {
            // first, make sure that the game exists by gameID
            GameData data = gameService.findGame(jreq.gameID());

            // case where supplied ID does not find a game, 401
            if (data == null) {
                // 401 error
                res.status(401);
                return new Gson().toJson(new SimpleResult("Error: unauthorized"));
            }

            // case where game color is not black / white, 400
            if (!jreq.playerColor().equals("BLACK") && (!jreq.playerColor().equals("WHITE"))) {
                // 400 error
                res.status(400);
                return new Gson().toJson(new SimpleResult("Error: bad request"));
            }

            // case where the desired color is already taken, 403
            if (jreq.playerColor().equals("BLACK")) {
                if (data.blackUsername() != null) {
                    // 403 error
                    res.status(403);
                    return new Gson().toJson(new SimpleResult("Error: already taken"));
                }
            }

            if (jreq.playerColor().equals("WHITE")) {
                if (data.whiteUsername() != null) {
                    // 403 error
                    res.status(403);
                    return new Gson().toJson(new SimpleResult("Error: already taken"));
                }
            }

            // success case
            // get the username for the current player
            AuthData authData = userService.getAuthData(authToken);
            String username = authData.username();
            GameData gameData = gameService.findGame(jreq.gameID());
            gameService.updateGameUser(username, gameData, jreq.playerColor());
            res.status(200);
            // return the result as a JSON
            return new Gson().toJson(new SimpleResult(null));
        }
        else {
            res.status(500);
            return new Gson().toJson(new SimpleResult("Error: an unexpected error occurred"));
        }
    }

}
