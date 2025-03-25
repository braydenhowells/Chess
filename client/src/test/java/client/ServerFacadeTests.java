package client;

import org.junit.jupiter.api.*;
import requests.*;
import results.CreateResult;
import results.LoginResult;
import results.SimpleResult;
import server.Server;
import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ui.ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ui.ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDb() {
        facade.clear();
    }

    @Test
    public void clear() {
        var result = facade.clear();
        assertNull(result.message());
    }

    @Test
    void register() {
        RegisterRequest request = new RegisterRequest("player1", "password", "p1@email.com");
        LoginResult result = facade.register(request);
        // make sure all the fields are good
        assertNotNull(result.authToken());
        assertNull(result.message());
        assertEquals("player1", result.username());
    }

    @Test
    void registerFail() {
        // register like normal
        RegisterRequest request = new RegisterRequest("player1", "password", "p1@email.com");
        facade.register(request);

        // now try to register again, should throw an error
        LoginResult result2 = facade.register(request);
        assertTrue(result2.message().contains("already taken"));
    }

    @Test
    public void create() {
        // register to get an auth token
        RegisterRequest rreq = new RegisterRequest("player1", "password", "p1@email.com");
        facade.register(rreq);
        CreateRequest creq = new CreateRequest("testGame");
        CreateResult result = facade.create(creq);
        assertEquals("1", result.gameID());

        CreateResult result2 = facade.create(creq);
        assertEquals("2", result2.gameID());
    }

    @Test
    public void createFail() {
        // register like normal
        RegisterRequest request = new RegisterRequest("player1", "password", "p1@email.com");
        facade.register(request);

        // use the token to logout
        facade.logout();

        // try to create now that we are logged out
        CreateRequest creq = new CreateRequest("testGame");
        CreateResult result2 = facade.create(creq);
        assertTrue(result2.message().contains("bad request"));
    }

    @Test
    public void logout() {
        // register like normal
        RegisterRequest request = new RegisterRequest("player1", "password", "p1@email.com");
        facade.register(request);

        // use the token to logout
        SimpleResult result2 = facade.logout();
        assertNull(result2.message());
    }

    @Test
    public void logoutFail() {
        // register like normal
        RegisterRequest request = new RegisterRequest("player1", "password", "p1@email.com");
        facade.register(request);
        // logout
        facade.logout();

        // try to log out without a token
        SimpleResult result2 = facade.logout();
        assertTrue(result2.message().contains("Error"));
    }



    @Test
    void login() {
        // register, then logout
        RegisterRequest request = new RegisterRequest("player1", "password", "p1@email.com");
        facade.register(request);
        facade.logout();
        // now try to log in
        var result2 = facade.login(new LoginRequest("player1", "password"));
        // see if it worked
        assertEquals("player1", result2.username());
    }

    @Test
    void loginFail() {
        // register, then logout
        RegisterRequest request = new RegisterRequest("player1", "password", "p1@email.com");
        facade.register(request);
        facade.logout();
        // now try to log in
        var result2 = facade.login(new LoginRequest("player1", "password_but_it_is_wrong"));
        // make sure we do not get a login
        assertTrue(result2.message().contains("Error"));
        assertNull(result2.username());
        assertNull(result2.authToken());
    }

    @Test
    public void list() {
        // register to get an auth token
        RegisterRequest rreq = new RegisterRequest("player", "password", "player@email.com");
        facade.register(rreq);
        CreateRequest creq = new CreateRequest("Game");
        facade.create(creq);
        facade.create(creq);
        var result = facade.list();
        assertEquals(2, result.games().size());
    }

    @Test
    public void listFail() {
        // register to get an auth token
        RegisterRequest rreq = new RegisterRequest("player", "password", "player@email.com");
        facade.register(rreq);
        CreateRequest creq = new CreateRequest("Game");
        facade.create(creq);
        var result = facade.list();
        // should be 1
        assertNotEquals(2, result.games().size());
        facade.create(creq);
        facade.create(creq);
        var result2 = facade.list();
        // should be 3
        assertNotEquals(2, result2.games().size());
    }


    @Test
    public void join() {
        // register to set up our player
        RegisterRequest rreq = new RegisterRequest("player", "password", "player@email.com");
        facade.register(rreq);
        // make a game
        CreateRequest creq = new CreateRequest("GamerzBeLike");
        facade.create(creq);
        // snag the id from the game
        var result = facade.list();
        String gameID = Integer.toString(result.games().getFirst().gameID());
        // try to join the game
        var result2 = facade.join(new JoinRequest("BLACK", gameID));
        assertNull(result2.message());
        var result3 = facade.list();
        String blackUsername = result3.games().getFirst().blackUsername();
        assertEquals("player", blackUsername);
    }

    @Test
    public void joinFail() {
        // register to set up our player
        RegisterRequest rreq = new RegisterRequest("player", "password", "player@email.com");
        facade.register(rreq);
        // make a game
        CreateRequest creq = new CreateRequest("GamerzBeLike");
        facade.create(creq);
        // use created game id for join
        var result = facade.list();
        String gameID = Integer.toString(result.games().getFirst().gameID());
        // try to join the game with bad color
        var result2 = facade.join(new JoinRequest("Rawr XD", gameID));
        // make sure we get an error
        assertTrue(result2.message().contains("Error"));
        // try to join with bad game ID
        var result3 = facade.join(new JoinRequest("WHITE", "69"));
        // make sure we get an error
        assertTrue(result3.message().contains("Error"));
        // we should not be in this game
        var result4 = facade.list();
        String blackUsername = result4.games().getFirst().blackUsername();
        assertNull(blackUsername);
    }

}
