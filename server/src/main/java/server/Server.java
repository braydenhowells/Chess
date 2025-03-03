package server;

import dataaccess.*;
import handlers.*;
import service.GameService;
import service.UserService;
import spark.*;


public class Server {

    // building block method to prevent multiple daos / services / handlers
    private final GameDAO gameDAO;
    public final AuthDAO authDAO;
    public final UserDAO userDAO;

    private final UserService userService;
    private final GameService gameService;


    private final MasterHandler handler;

    public Server() {
        this.gameDAO = new MemoryGameDao();
        this.authDAO = new MemoryAuthDao();
        this.userDAO = new MemoryUserDao();

        this.userService = new UserService(userDAO, authDAO);
        this.gameService = new GameService(gameDAO);

        // i am choosing to have only 1 handler
        this.handler = new MasterHandler(userService, gameService);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // yayyy api methods
        Spark.post("/user", (req, res) -> handler.register(req, res));
        Spark.delete("/db", (req, res) -> handler.clear(res)); // no api body, no auth
        Spark.post("/session", (req, res) -> handler.login(req, res));
        Spark.delete("/session", (req, res) -> handler.logout(req.headers("authorization"), res));
        Spark.post("/game", (req, res) -> handler.create(req, res));
        Spark.get("/game", (req, res) -> handler.list(req.headers("authorization"), res));

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
