package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.LoginRequest;
import requests.RegisterRequest;
import results.LoginResult;
import results.SimpleResult;

import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    static UserDAO userDAO = new SQLUserDao();
    static AuthDAO authDAO = new SQLAuthDao();
    static AuthService authService = new AuthService(authDAO);
    static UserService userService = new UserService(userDAO, authDAO, authService);

    @BeforeEach
    void reset() throws SQLException {
        userDAO.clear();
        authDAO.clear();
    }

    @Test
    void register() {
        RegisterRequest request = new RegisterRequest("user", "pass", "email");
        LoginResult result = userService.register(request);
        assertEquals(request.username(), result.username());
    }

    @Test
    void login() {
        userService.register(new RegisterRequest("user", "pass", "email"));
        LoginRequest request = new LoginRequest("user", "pass");
        LoginResult result = userService.login(request);
        assertEquals(request.username(), result.username());
    }

    @Test
    void logout() throws SQLException {
        authDAO.createAuth(new AuthData("token", "user"));
        userDAO.createUser(new UserData("user", "pass", "email"));
        SimpleResult result = userService.logout("token");
        assertNull(result.message());
    }

    @Test
    void userClear() throws SQLException {
        userService.register(new RegisterRequest("user", "pass", "email"));
        userService.register(new RegisterRequest("user1", "pass1", "email1"));
        userService.userClear();
        assertEquals(0, userDAO.getAllUsers().size());
    }

    @Test
    void authClear() throws SQLException {
        AuthData data = new AuthData("epicToken", "gang");
        AuthData data1 = new AuthData("epicToken1", "gang2electricBoogaloo");
        authDAO.createAuth(data);
        authDAO.createAuth(data1);
        authService.authClear();
        assertEquals(0, authDAO.getAllAuth().size());
    }

    @Test
    void getAuthData() throws SQLException {
        AuthData data = new AuthData("epicToken", "epicUser");
        authDAO.createAuth(data);
        AuthData result = authService.getAuthData("epicToken");
        assertEquals(data, result);
    }

    @Test
    void registerFail() throws SQLException {
        userDAO.createUser(new UserData("user", "pass", "email"));
        RegisterRequest request = new RegisterRequest("user", "pass", "email");
        LoginResult result = userService.register(request);
        assertTrue(result.message().contains("Error"));
    }

    @Test
    void loginFail() {
        userService.register(new RegisterRequest("user1", "pass", "email"));
        LoginRequest request = new LoginRequest("user1", "pass_wacky");
        LoginResult result = userService.login(request);
        assertTrue(result.message().contains("Error"));
    }

    @Test
    void logoutFail() throws SQLException {
        authDAO.createAuth(new AuthData("token", "user"));
        SimpleResult result = userService.logout("boof pack");
        assertTrue(result.message().contains("Error"));
    }

    @Test
    void getAuthDataFail() {
        AuthData result = authService.getAuthData("wacky_token_hopefully_this_returns_null");
        assertNull(result);
    }
}
