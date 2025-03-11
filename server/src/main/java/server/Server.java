package server;

import dataaccess.*;
import handlers.*;
import service.AuthService;
import service.GameService;
import service.UserService;
import spark.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Server {

    public final AuthDAO authDAO;
    public final UserDAO userDAO;
    public final GameDAO gameDAO;
    private final MasterHandler handler;

    public Server() {
        // building block method to prevent multiple daos / services / handlers
        this.gameDAO = new MemoryGameDao();
        this.authDAO = new MemoryAuthDao();
        this.userDAO = new MemoryUserDao();

        AuthService authService = new AuthService(authDAO);
        UserService userService = new UserService(userDAO, authDAO, authService);
        GameService gameService = new GameService(gameDAO, authService);

        // I am choosing to have only 1 handler
        // I feel like the handlers do very similar things and I didn't want duplicate code
        this.handler = new MasterHandler(userService, gameService, authService);
    }

    public int run(int desiredPort) throws SQLException {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        configureDatabase();

        // yayyy api methods
        Spark.post("/user", (req, res) -> handler.register(req, res));
        Spark.delete("/db", (req, res) -> handler.clear(res)); // no api body, no auth
        Spark.post("/session", (req, res) -> handler.login(req, res));
        Spark.delete("/session", (req, res) -> handler.logout(req.headers("authorization"), res));
        Spark.post("/game", (req, res) -> handler.create(req, res));
        Spark.get("/game", (req, res) -> handler.list(req.headers("authorization"), res));
        Spark.put("/game", (req, res) -> handler.join(req, res));

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "NewSecurePassword");
    }

    void makeSQLCalls() throws SQLException {
        try (var conn = getConnection()) {
            // Execute SQL statements on the connection here
        }
    }


    void configureDatabase() throws SQLException {
        try (var conn = getConnection()) {
            var createDbStatement = conn.prepareStatement("CREATE DATABASE IF NOT EXISTS pet_store");
            createDbStatement.executeUpdate();

            conn.setCatalog("pet_store");

            var createPetTable = """
            CREATE TABLE  IF NOT EXISTS pet (
                id INT NOT NULL AUTO_INCREMENT,
                name VARCHAR(255) NOT NULL,
                type VARCHAR(255) NOT NULL,
                PRIMARY KEY (id)
            )""";


            try (var createTableStatement = conn.prepareStatement(createPetTable)) {
                createTableStatement.executeUpdate();
            }
        }
    }

}
