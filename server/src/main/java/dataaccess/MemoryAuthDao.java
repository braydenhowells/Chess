package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDao implements AuthDAO {
    // hashmap of key:value as authToken:AuthData
    private final static HashMap<String, AuthData> ALL_AUTHS = new HashMap<>();

    public void createAuth(AuthData data) {
        ALL_AUTHS.put(data.authToken(), data);
    }

    public void clear() {
        ALL_AUTHS.clear();
    }

    public AuthData findAuthData(String authToken) {
        return ALL_AUTHS.get(authToken);
    }

    public void deleteAuthData(AuthData data) {
        ALL_AUTHS.remove(data.authToken());
    }
}

