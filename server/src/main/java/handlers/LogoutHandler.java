package handlers;

import com.google.gson.Gson;
import results.SimpleResult;
import service.AuthService;
import spark.Request;
import spark.Response;

public class LogoutHandler {
    private final AuthService authService;

    public LogoutHandler(AuthService authService) {
        this.authService = authService;
    }

    public Object logout(Request req, Response res) {
        return new Gson().toJson(new SimpleResult(null));
    }
}
