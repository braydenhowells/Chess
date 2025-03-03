package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void createAuth(AuthData data);
    void clear();
    AuthData findAuthData(String data);
    void deleteAuthData(AuthData data);
}
