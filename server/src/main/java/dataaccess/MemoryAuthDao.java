package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;

public class MemoryAuthDao implements AuthDAO {

    private HashMap<String, AuthData> allAuths = new HashMap<>();

    public void createAuth(AuthData data) {
        allAuths.put(data.authToken(), data);
    }
}

