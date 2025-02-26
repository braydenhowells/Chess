package server;

import dataaccess.*;
import handlers.ClearHandler;
import handlers.RegisterHandler;
import spark.*;

// place to initialize the dao hashmaps and pass that as data access to make sure we use the same in our classes going forward

public class Server {

    public GameDAO gameDAO = new MemoryGameDao();
    public AuthDAO authDAO = new MemoryAuthDao();
    public UserDAO userDAO = new MemoryUserDao();


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // instructions for urls to methods
        Spark.post("/user", (req, res) -> (new RegisterHandler(userDAO, authDAO)).handleRequest(req, res));
        Spark.delete("/db", (req, res) -> (new ClearHandler(userDAO, gameDAO, authDAO)).clearRequest(req, res));

        // Register your endpoints and handle exceptions here.

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
