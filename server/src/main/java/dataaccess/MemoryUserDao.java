package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDao implements UserDAO {

    private HashMap<String, UserData> allUsers = new HashMap<>();

    public UserData getUser(String username) {
        return allUsers.get(username); // return null if not found
    }

    public void createUser(UserData data) {
        allUsers.put(data.username(), data);
    }
}
