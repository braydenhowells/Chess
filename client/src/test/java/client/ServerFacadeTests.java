package client;

import org.junit.jupiter.api.*;
import requests.RegisterRequest;
import results.SimpleResult;
import server.Server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


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
    public void clearDb() {
        // fill this with a clear
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    void register() throws Exception {
        // pasted from the md
        RegisterRequest request = new RegisterRequest("player1", "password", "p1@email.com");
        SimpleResult result = facade.register(request);
        assertNull(result.message());

    }

}
