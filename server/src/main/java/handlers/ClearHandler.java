package handlers;

import com.google.gson.Gson;
import results.ClearResult;
import service.AuthService;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;

public class ClearHandler {

    public Object clearRequest(Request req, Response res) {

        if (req.body() == null) { // deserialize before checking
            res.status(500);
            return new Gson().toJson(new ClearResult("Error: this endpoint requires an empty body"));
        }
        UserService userService = new UserService();
        userService.clear();
        AuthService authService = new AuthService();
        authService.clear();
        GameService gameService = new GameService();
        gameService.clear();

        res.status(200);
        return new Gson().toJson(null);
    }
}
