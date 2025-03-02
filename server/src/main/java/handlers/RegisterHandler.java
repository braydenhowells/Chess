package handlers;

import requests.RegisterRequest;
import com.google.gson.Gson;
import results.LoginResult;
import service.UserService;
import spark.Request;
import spark.Response;

public class RegisterHandler {

    private final UserService userService;

    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }

    public Object register(Request req, Response res){
        RegisterRequest rreq = new Gson().fromJson(req.body(), RegisterRequest.class);

        // make a check for null values in rreq, possibly return 400 bad request, missing a piece of info
        if (rreq.email() == null || rreq.email().isEmpty() ||
                rreq.username() == null || rreq.username().isEmpty() ||
                rreq.password() == null || rreq.password().isEmpty()) {

            res.status(400);
            return new Gson().toJson(new LoginResult("Error: at least one field is empty. Please try again", null, null));
        }

        LoginResult result = userService.register(rreq);

        // 200
        if (result.message() == null) {
            res.status(200);
        }

        // 403
        if (result.message()!= null) {
            if (result.message().contains("taken")) {
                res.status(403);
            }
            else {
                res.status(500);
            }
        }

        // return the register result when finished, as a JSON
        return new Gson().toJson(result);
    }
}
