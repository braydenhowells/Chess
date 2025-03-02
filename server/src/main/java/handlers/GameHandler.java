package handlers;

import com.google.gson.Gson;
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
        // verify auth token
        String authToken = req.headers("authorization");

        // 400
        if (authToken.isEmpty()) {
            res.status(400);
            return new Gson().toJson(new CreateResult(null, "Error: no authToken provided in header"));
        }



        // if verified, create game


        // placeholder, MUST RETURN A JSON
        return new CreateResult(null, null); // this is wrong
    }

}
