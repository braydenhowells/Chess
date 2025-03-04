package service;

import dataaccess.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.RegisterRequest;
import service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserServiceTest {
    static UserDAO userDAO = new MemoryUserDao();
    static AuthDAO authDAO = new MemoryAuthDao();
    static UserService service = new UserService(userDAO, authDAO);

    @BeforeEach
    void reset() {
    }

    // assert throws

    @Test
    void register() {
    }

    @Test
    void login() {
    }

    @Test
    void logout() {
    }

    @Test
    void userClear() throws DataAccessException {
        service.register(new RegisterRequest("user", "pass", "email"));
        service.register(new RegisterRequest("user1", "pass1", "email1"));

        service.userClear();
        assertEquals(0, userDAO.getAllUsers().size());
    }

    @Test
    void authClear() {
    }

    @Test
    void getAuthData() {
    }

}