package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class AuthDAOTest {
    static AuthDAO authDAO = new SQLAuthDao();

    @BeforeEach
    void reset() throws SQLException {
        authDAO.clear();
    }

    @Test
    void createAuth() throws SQLException {
        AuthData authData = new AuthData("epicToken", "me");
        authDAO.createAuth(authData);
        AuthData result = authDAO.findAuthData(authData.authToken());
        assertEquals("epicToken", result.authToken());
        assertEquals("me", result.username());
    }

    @Test
    void clear() throws SQLException {
        AuthData authData = new AuthData("epicToken", "me");
        AuthData authData2 = new AuthData("epicToken2", "me2");
        authDAO.createAuth(authData);
        authDAO.createAuth(authData2);

        authDAO.clear();
        assertEquals(0, authDAO.getAllAuth().size());
    }

    @Test
    void findAuthData() throws SQLException {
        AuthData authData = new AuthData("monstersInc", "me");
        authDAO.createAuth(authData);
        AuthData result = authDAO.findAuthData(authData.authToken());
        assertEquals("monstersInc", result.authToken());
        assertEquals("me", result.username());
    }

    @Test
    void deleteAuthData() throws SQLException {
        AuthData authData = new AuthData("epicToken", "me");
        authDAO.createAuth(authData);
        authDAO.deleteAuthData(authData);
        assertEquals(0, authDAO.getAllAuth().size());
    }

    @Test
    void getAllAuth() throws SQLException {
        AuthData authData = new AuthData("epicToken", "me");
        AuthData authData2 = new AuthData("epicToken2", "me2");
        authDAO.createAuth(authData);
        authDAO.createAuth(authData2);
        assertEquals(2, authDAO.getAllAuth().size());
    }

    @Test
    void createAuthFail() {
        boolean fail = false;
        AuthData authData = new AuthData(null, "me");
        try {
            authDAO.createAuth(authData);
        } catch (SQLException e) {
            fail = true;
        }
        assertTrue(fail);
    }

    @Test
    void findAuthDataFail() throws SQLException {
        AuthData authData = new AuthData("monstersInc", "me");
        authDAO.createAuth(authData);
        AuthData result = authDAO.findAuthData("notMonstersInc");
        assertNull(result);
    }

    @Test
    void deleteAuthDataFail() throws SQLException {
        AuthData authData = new AuthData("epicToken", "me");
        authDAO.createAuth(authData);
        AuthData authDataWrong = new AuthData("nada", "nada");
        authDAO.deleteAuthData(authDataWrong);
        assertEquals(1, authDAO.getAllAuth().size());
    }

    @Test
    void getAllAuthFail() throws SQLException {
        AuthData authData2 = new AuthData("epicToken2", "me2");
        authDAO.createAuth(authData2);
        assertNotEquals(2, authDAO.getAllAuth().size());
    }
}