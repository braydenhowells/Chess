package handlers;

import com.google.gson.Gson;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;

public class ClearHandler {

    private final UserService userService;
    private final GameService gameService;

    public ClearHandler(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;

    }

    public Object clear(Request req, Response res) {
        // idk how a 500 error would even look for this one. not sure where to put it

        userService.authClear();
        userService.userClear();
        gameService.clear();

        res.status(200);
        return new Gson().toJson(null);
    }
}
