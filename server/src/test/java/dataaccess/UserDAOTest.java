package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {
    static UserDAO userDAO = new SQLUserDao();

    @BeforeEach
    void reset() throws SQLException {
        userDAO.clear();
    }


    @Test
    void getUser() throws SQLException {
        UserData userData = new UserData("bowserJunior", "peach", "email");
        userDAO.createUser(userData);
        UserData result = userDAO.getUser("bowserJunior");
        assertEquals(result.username(), userData.username());
        assertEquals(result.email(), userData.email());
        assertEquals(result.password(), userData.password());
    }

    @Test
    void createUser() throws SQLException {
        UserData userData = new UserData("bowser", "peach", "email");
        userDAO.createUser(userData);
        UserData result = userDAO.getUser("bowser");
        assertEquals(result.username(), userData.username());
        assertEquals(result.email(), userData.email());
        assertEquals(result.password(), userData.password());
    }

    @Test
    void clear() throws SQLException {
        UserData userData = new UserData("bowser", "peach", "email");
        userDAO.createUser(userData);
        UserData userData2 = new UserData("bowserJunior", "peach", "emailJunior");
        userDAO.createUser(userData2);
        userDAO.clear();
        assertEquals(0, userDAO.getAllUsers().size());
    }

    @Test
    void getAllUsers() throws SQLException {
        UserData userData = new UserData("mario", "peach", "emailForMario");
        userDAO.createUser(userData);
        UserData userData2 = new UserData("bowserJunior", "peach", "emailJunior");
        userDAO.createUser(userData2);
        assertEquals(2, userDAO.getAllUsers().size());
    }

    @Test
    void getUserFail() throws SQLException {
        UserData userData = new UserData("bowserJunior", "peach", "email");
        userDAO.createUser(userData);
        UserData result = userDAO.getUser("bowser");
        assertNull(result);
    }

    @Test
    void createUserFail() throws SQLException {
        boolean fail = false;
        try {
            userDAO.createUser(null);
        }
        catch (NullPointerException e) {
            fail = true;
        }
        assertEquals(true, fail);
    }

    @Test
    void getAllUsersFail() throws SQLException {
        UserData userData = new UserData("mario", "peach", "emailForMario");
        userDAO.createUser(userData);
        assertNotEquals(2, userDAO.getAllUsers().size());
    }

}