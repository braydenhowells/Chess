package dataaccess;

import model.UserData;

import java.util.ArrayList;
import java.util.HashMap;

public class MemoryUserDao implements UserDAO {

    private final static HashMap<String, UserData> ALL_USERS = new HashMap<>();
    // this keeps our database the same, we can update and clear it, but we can't mess with the reference or make a new one

    public UserData getUser(String username) {
        return ALL_USERS.get(username); // return null if not found
    }

    public void createUser(UserData data) {
        ALL_USERS.put(data.username(), data);
    }

    public void clear() {
        ALL_USERS.clear();
    }

    public ArrayList<UserData> getAllUsers() {
        return new ArrayList<>(ALL_USERS.values());
    }
}
