package handlers;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import service.AuthService;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;

public class ClearHandler {

    private final UserService userService;
    private final AuthService authService;
    private final GameService gameService;

    public ClearHandler(UserService userService, AuthService authService, GameService gameService) {
        this.userService = userService;
        this.authService = authService;
        this.gameService = gameService;

    }

    public Object clear(Request req, Response res) {
        // idk how a 500 error would even look for this one. not sure where to put it

        userService.clear();
        authService.clear();
        gameService.clear();

        res.status(200);
        return new Gson().toJson(null);
    }
}
