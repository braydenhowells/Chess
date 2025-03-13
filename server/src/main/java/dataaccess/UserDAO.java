package dataaccess;

import model.UserData;

import java.sql.SQLException;
import java.util.ArrayList;

public interface UserDAO {

    public UserData getUser(String username) throws SQLException;
    public void createUser(UserData data) throws SQLException;
    public void clear() throws SQLException;
    public ArrayList<UserData> getAllUsers() throws SQLException;
}

