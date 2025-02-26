package handlers;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import requests.RegisterRequest;
import com.google.gson.Gson;
import results.RegisterResult;
import service.UserService;
import spark.Request;
import spark.Response;

public class RegisterHandler {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public RegisterHandler(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public Object handleRequest(Request req, Response res){
        RegisterRequest rreq = new Gson().fromJson(req.body(), RegisterRequest.class);

        // make a check for null values in rreq, possibly return 400 bad request, missing a piece of info
        if (rreq.email().isEmpty() || rreq.username().isEmpty() || rreq.password().isEmpty()) {
            res.status(400);
            return new Gson().toJson(new RegisterResult("Error: at least one field is empty. Please try again", null, null));
        }

        UserService service = new UserService(userDAO, authDAO);
        RegisterResult result = service.register(rreq);

        // also update the status code success
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
    };

}
