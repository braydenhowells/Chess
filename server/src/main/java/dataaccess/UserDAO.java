package dataaccess;

import model.UserData;

import java.util.ArrayList;

public interface UserDAO {

    public UserData getUser(String username);
    public void createUser(UserData data);
    public void clear();
    public ArrayList<UserData> getAllUsers();
}

