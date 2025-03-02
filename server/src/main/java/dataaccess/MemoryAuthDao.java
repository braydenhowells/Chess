package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDao implements AuthDAO {
    // hashmap of key:value as authToken:AuthData
    private final static HashMap<String, AuthData> allAuths = new HashMap<>();

    public void createAuth(AuthData data) {
        allAuths.put(data.authToken(), data);
    }

    public void clear() {
        allAuths.clear();
    }

    public AuthData findAuthData(String authToken) {
        return allAuths.get(authToken);
    }

    public void deleteAuthData(AuthData data) {
        allAuths.remove(data.authToken());
    }
}

