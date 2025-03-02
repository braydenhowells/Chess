package handlers;

import com.google.gson.Gson;
import model.AuthData;
import requests.CreateRequest;
import requests.LoginRequest;
import results.CreateResult;
import results.SimpleResult;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;

public class GameHandler {
    private final GameService gameService;
    private final UserService userService;

    public GameHandler(UserService userService, GameService gameService) {
        this.gameService = gameService;
        this.userService = userService;
    }

    public void list() {
        // make this later, definitely not void
        // return value should be a list of game objects, not sure how that works yet
    }

    public Object create(Request req, Response res) {
        CreateRequest creq = new Gson().fromJson(req.body(), CreateRequest.class);

        // verify auth token
        String authToken = req.headers("authorization");

        // 400
        if (authToken.isEmpty() || creq.gameName() == null || creq.gameName().isEmpty()) {
            res.status(400);
            return new Gson().toJson(new CreateResult(null, "Error: bad request"));
        }

        AuthData data = userService.getAuthData(authToken);

        // 401
        if (data == null) {
            res.status(401);
            return new Gson().toJson(new CreateResult(null, "Error: unauthorized"));
        }

        // if verified, create game
        CreateResult result = gameService.create(creq.gameName());
        res.status(200);


        // return the result as a JSON
        return new Gson().toJson(result);
    }

}
