package client;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import requests.CreateRequest;
import requests.RegisterRequest;
import results.CreateResult;
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
        // fill this in later after list games is working
        Assertions.assertTrue(true);
    }

    @Test
    void register() throws Exception {
        RegisterRequest request = new RegisterRequest("player1", "password", "p1@email.com");
        SimpleResult result = facade.register(request);
        assertNull(result.message());
    }

    @Test
    void registerFail() throws Exception {
        // register like normal
        RegisterRequest request = new RegisterRequest("player1", "password", "p1@email.com");
        SimpleResult result = facade.register(request);
        assertNull(result.message());

        // now try to register again, should throw an error
        try {
            facade.register(request);
        } catch (ResponseException e) {
            String message = e.getMessage();
            assertTrue(message.contains("already taken"));
        }
    }

    @Test
    public void create() throws Exception {
        CreateRequest request = new CreateRequest("testGame");
        CreateResult result = facade.create(request);
        assertNull(result.message());
    }


}
