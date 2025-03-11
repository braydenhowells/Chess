package handlers;

import com.google.gson.Gson;
import model.GameData;
import requests.*;
import results.CreateResult;
import results.ListResult;
import results.LoginResult;
import results.SimpleResult;
import service.AuthService;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;


public class MasterHandler {

    private final UserService userService;
    private final GameService gameService;
    private final AuthService authService;

    public MasterHandler(UserService userService, GameService gameService, AuthService authService) {
        this.userService = userService;
        this.gameService = gameService;
        this.authService = authService;
    }

    public Object clear(Response res) {
        // idk how a 500 error would even look for this one. not sure where to put it
        authService.authClear();
        userService.userClear();
        gameService.clear();
        res.status(200);
        return new Gson().toJson(null);
    }

    public Object create(Request req, Response res) {
        // make a request
        CreateRequest creq = new Gson().fromJson(req.body(), CreateRequest.class);
        // get token
        String authToken = req.headers("authorization");
        // see if we can create a game
        CreateResult result = gameService.create(creq.gameName(), authToken);

        return formatGson(res, result);
    }

    public Object list(String authToken, Response res) {
        ListResult result = gameService.getGames(authToken);
        return formatGson(res, result);
    }

    public Object logout(String authToken, Response res) {
        SimpleResult result = userService.logout(authToken);
        // success case
        return formatGson(res, result);
    }

    private int determineErrorCode(String message) {
        if (message.contains("bad request")) {return 400;}
        if (message.contains("unauthorized")) {return 401;}
        if (message.contains("forbidden") || message.contains("taken")) {return 403;}
        return 500;
    }

    private Object formatGson(Response res, Object result) {
        // simple case
        if (result instanceof SimpleResult simpleResult) {
            if (simpleResult.message() == null || simpleResult.message().isEmpty()) {
                res.status(200);
            } else {
                res.status(determineErrorCode(simpleResult.message()));
            }
            return new Gson().toJson(simpleResult);
        }

        // list case
        else if (result instanceof ListResult listResult) {
            if (listResult.message() == null || listResult.message().isEmpty()) {
                res.status(200);
            } else {
                res.status(determineErrorCode(listResult.message()));
            }
            return new Gson().toJson(listResult);
        }

        // create case
        else if (result instanceof CreateResult createResult) {
            if (createResult.message() == null || createResult.message().isEmpty()) {
                res.status(200);
            } else {
                res.status(determineErrorCode(createResult.message()));
            }
            return new Gson().toJson(createResult);
        }

        // login case
        else if (result instanceof LoginResult loginResult) {
            if (loginResult.message() == null || loginResult.message().isEmpty()) {
                res.status(200);
            } else {
                res.status(determineErrorCode(loginResult.message()));
            }
            return new Gson().toJson(loginResult);
        }

        // Fallback for unexpected cases
        res.status(500);
        return new Gson().toJson(new SimpleResult("Error: an unexpected error occurred"));
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

        return formatGson(res, result);
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
        return formatGson(res, result);
    }

    public Object join(Request req, Response res) {
        JoinRequest jreq = new Gson().fromJson(req.body(), JoinRequest.class);
        GameData gameData = gameService.findGame(jreq.gameID());
        String color = jreq.playerColor();

        SimpleResult result = gameService.join(gameData,color, req.headers("authorization"), jreq);

        // success
        return formatGson(res, result);
    }

}
