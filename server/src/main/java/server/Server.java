package server;

import dataaccess.*;
import handlers.*;
import service.GameService;
import service.UserService;
import spark.*;

// place to initialize the dao hashmaps and pass that as data access to make sure we use the same in our classes going forward

public class Server {

    // building block method to prevent multiple daos / services / handlers
    private final GameDAO gameDAO;
    public final AuthDAO authDAO;
    public final UserDAO userDAO;

    private final UserService userService;
    private final GameService gameService;

    private final RegisterHandler registerHandler;
    private final ClearHandler clearHandler;
    private final LoginHandler loginHandler;
    private final LogoutHandler logoutHandler;
    private final GameHandler gameHandler;

    public Server() {
        this.gameDAO = new MemoryGameDao();
        this.authDAO = new MemoryAuthDao();
        this.userDAO = new MemoryUserDao();

        this.userService = new UserService(userDAO, authDAO);
        this.gameService = new GameService(gameDAO);

        this.registerHandler = new RegisterHandler(userService);
        this.clearHandler = new ClearHandler(userService, gameService);
        this.loginHandler = new LoginHandler(userService);
        this.logoutHandler = new LogoutHandler(userService);
        this.gameHandler = new GameHandler(userService, gameService);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // instructions for urls to methods
        Spark.post("/user", (req, res) -> registerHandler.register(req, res));
        Spark.delete("/db", (req, res) -> clearHandler.clear(req, res));
        Spark.post("/session", (req, res) -> loginHandler.login(req, res));
        Spark.delete("/session", (req, res) -> logoutHandler.logout(req.headers("authorization"), res));
        Spark.post("/game", (req, res) -> gameHandler.create(req, res));
        Spark.get("/game", (req, res) -> gameHandler.list(req.headers("authorization"), res));

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
