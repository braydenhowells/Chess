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

    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;


    public ClearHandler(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public Object clear(Request req, Response res) {
        // idk how a 500 error would even look for this one. not sure where to put it

        UserService userService = new UserService(userDAO, authDAO);
        userService.clear();
        AuthService authService = new AuthService(authDAO);
        authService.clear();
        GameService gameService = new GameService(gameDAO);
        gameService.clear();

        res.status(200);
        return new Gson().toJson(null);
    }
}
