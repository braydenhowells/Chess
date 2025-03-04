package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.LoginRequest;
import requests.LogoutRequest;
import requests.RegisterRequest;
import results.LoginResult;
import results.SimpleResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserServiceTest {
    static UserDAO userDAO = new MemoryUserDao();
    static AuthDAO authDAO = new MemoryAuthDao();
    static UserService service = new UserService(userDAO, authDAO);

    @BeforeEach
    void reset() {
        authDAO.clear();
        userDAO.clear();
    }

    // assert throws

    @Test
    void register() {
        RegisterRequest request = new RegisterRequest("user", "pass", "email");
        LoginResult result = service.register(request);
        assertEquals(result.username(), request.username());
    }

    @Test
    void login() {
        service.register(new RegisterRequest("user", "pass", "email"));
        LoginRequest request = new LoginRequest("user", "pass");
        LoginResult result = service.login(request);
        assertEquals(result.username(), request.username());
    }

    @Test
    void logout() {
        authDAO.createAuth(new AuthData("token", "user"));
        userDAO.createUser(new UserData("user", "pass", "email"));

        SimpleResult result = service.logout(new LogoutRequest("token"));
        assertNull(result.message());

    }

    @Test
    void userClear() throws DataAccessException {
        service.register(new RegisterRequest("user", "pass", "email"));
        service.register(new RegisterRequest("user1", "pass1", "email1"));

        service.userClear();
        assertEquals(0, userDAO.getAllUsers().size());
    }

    @Test
    void authClear() throws DataAccessException {
        AuthData data = new AuthData("epicToken", "epicUser");
        AuthData data1 = new AuthData("epicToken1", "epicUser1");
        authDAO.createAuth(data);
        authDAO.createAuth(data1);

        service.authClear();
        assertEquals(0, authDAO.getAllAuth().size());
    }

    @Test
    void getAuthData() throws DataAccessException {
        AuthData data = new AuthData("epicToken", "epicUser");
        authDAO.createAuth(data);
        AuthData result = service.getAuthData("epicToken");

        assertEquals(data, result);
    }


}