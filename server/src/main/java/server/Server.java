package server;

import handlers.ClearHandler;
import handlers.RegisterHandler;
import spark.*;

// place to initilaize the dao hashmaps and pass that as data access to make sure we use the same in our classes going forward

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // instructions for urls to methods
        Spark.post("/user", (req, res) -> (new RegisterHandler()).handleRequest(req, res));
        Spark.delete("/db", (req, res) -> (new ClearHandler()).clearRequest(req, res));

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
