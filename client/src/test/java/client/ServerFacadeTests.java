package client;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import requests.CreateRequest;
import requests.RegisterRequest;
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
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
        facade = new ui.ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDb() throws Exception {
        facade.clear();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void clear() {
        // fill this in later after list games is working. I think we good on it tho
        Assertions.assertTrue(true);
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
        // try to create without getting an auth token
        CreateRequest creq = new CreateRequest("testGame");
        CreateResult result = facade.create(creq);
        assertTrue(result.message().contains("bad request"));
    }

}
