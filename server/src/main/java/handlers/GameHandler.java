package handlers;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import requests.CreateRequest;
import requests.LoginRequest;
import results.CreateResult;
import results.ListResult;
import results.SimpleResult;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;

import java.util.List;


public class GameHandler {
    private final GameService gameService;
    private final UserService userService;

    public GameHandler(UserService userService, GameService gameService) {
        this.gameService = gameService;
        this.userService = userService;
    }

    public Object list(String authToken, Response res) {
        // verify auth token
        // 400
        if (authToken.isEmpty()) {
            res.status(400);
            return new Gson().toJson(new SimpleResult("Error: bad request"));
        }

        AuthData data = userService.getAuthData(authToken);

        // 401
        if (data == null) {
            res.status(401);
            return new Gson().toJson(new SimpleResult("Error: unauthorized"));
        }

        // now we know that the token is legit. let's proceed
        ListResult games = gameService.getGames();
        res.status(200);
        // jsonify that sucker
        return new Gson().toJson(games);
    }

    public Object create(Request req, Response res) {
        CreateRequest creq = new Gson().fromJson(req.body(), CreateRequest.class);

        // verify auth token
        String authToken = req.headers("authorization");

        // 400
        if (authToken.isEmpty() || creq.gameName() == null || creq.gameName().isEmpty()) {
            res.status(400);
            return new Gson().toJson(new SimpleResult("Error: bad request"));
        }

        AuthData data = userService.getAuthData(authToken);

        // 401
        if (data == null) {
            res.status(401);
            return new Gson().toJson(new SimpleResult("Error: unauthorized"));
        }

        // if verified, create game
        CreateResult result = gameService.create(creq.gameName());
        res.status(200);

        // return the result as a JSON
        return new Gson().toJson(result);
    }

}
