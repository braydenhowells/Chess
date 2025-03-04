package dataaccess;

import model.AuthData;

import java.util.ArrayList;

public interface AuthDAO {
    void createAuth(AuthData data);
    void clear();
    AuthData findAuthData(String data);
    void deleteAuthData(AuthData data);
    ArrayList<AuthData> getAllAuth();
}
