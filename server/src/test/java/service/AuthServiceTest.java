package service;

import dataaccess.AuthDAO;
import dataaccess.SQLAuthDao;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {
    static AuthDAO authDAO = new SQLAuthDao();
    static AuthService authService = new AuthService(authDAO);

    @BeforeEach
    void reset() throws SQLException {
        authDAO.clear();
    }

    @Test
    void verifyAuth() throws SQLException {
        AuthData authData = new AuthData("validToken", "user123");
        authDAO.createAuth(authData);
        String verification = authService.verifyAuth("validToken", false, null);
        assertEquals(verification, "verified");
    }

    @Test
    void verifyAuthFail() throws SQLException {
        String verification = authService.verifyAuth("goofy_wacky", false, null);
        assertTrue(verification.contains("Error"));
    }

    @Test
    void getAuthData() throws SQLException {
        AuthData authData = new AuthData("authy", "uso");
        authDAO.createAuth(authData);
        AuthData retrieved = authService.getAuthData("authy");
        assertNotNull(retrieved);
        assertEquals(authData.authToken(), retrieved.authToken());
        assertEquals(authData.username(), retrieved.username());
    }

    @Test
    void getAuthDataFail() {
        assertNull(authService.getAuthData("goofy_wacky"));
    }

    @Test
    void authClear() throws SQLException {
        authDAO.createAuth(new AuthData("auth1", "user1"));
        authDAO.createAuth(new AuthData("auth2", "user2"));
        authService.authClear();
        assertEquals(0, authDAO.getAllAuth().size());
    }
}
