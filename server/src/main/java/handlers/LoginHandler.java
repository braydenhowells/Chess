package handlers;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import requests.LoginRequest;
import requests.RegisterRequest;
import results.LoginResult;
import results.RegisterResult;
import service.UserService;
import spark.Request;
import spark.Response;

public class LoginHandler {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public LoginHandler(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public Object login(Request req, Response res) {
        LoginRequest lreq = new Gson().fromJson(req.body(), LoginRequest.class);

        // make a check for null values in rreq, possibly return 400 bad request, missing a piece of info
        if (lreq.username() == null || lreq.username().isEmpty() ||
                lreq.password() == null || lreq.password().isEmpty()) {

            res.status(400);
            return new Gson().toJson(new RegisterResult("Error: at least one field is empty. Please try again", null, null));
        }

        UserService userService = new UserService(userDAO, authDAO);
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
}
