package handlers;

import com.google.gson.Gson;
import requests.LogoutRequest;
import results.SimpleResult;
import service.UserService;
import spark.Response;

public class LogoutHandler {
    private final UserService userService;

    public LogoutHandler(UserService userService) {
        this.userService = userService;
    }

    public Object logout(String authToken, Response res) {
        // 400
        if (authToken.isEmpty()) {
            res.status(400);
            return new Gson().toJson(new SimpleResult("Error: no authToken provided in header"));
        }

        LogoutRequest lreq = new LogoutRequest(authToken);
        SimpleResult result = userService.logout(lreq);

        // 401 and 500
        if (result.message() != null) {
            if (result.message().contains("unauthorized")) {
                res.status(401);
            }
            else {
                res.status(500);
            }
        }
        // success 200
        else {
            res.status(200);
        }
        // finish her up
        return new Gson().toJson(result);
    }

}
