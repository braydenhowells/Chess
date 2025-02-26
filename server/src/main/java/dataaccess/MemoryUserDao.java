package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDao implements UserDAO {

    private final static HashMap<String, UserData> allUsers = new HashMap<>();
    // this keeps our database the same, we can update and clear it, but we can't mess with the reference or make a new one

    public UserData getUser(String username) {
        return allUsers.get(username); // return null if not found
    }

    public void createUser(UserData data) {
        allUsers.put(data.username(), data);
    }

    public void clear() {
        allUsers.clear();
    }
}
