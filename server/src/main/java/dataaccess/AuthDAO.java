package dataaccess;

import model.AuthData;

public interface AuthDAO {
    public void createAuth(AuthData data);
    public void clear();
    public AuthData findAuthData(String data);
    public void deleteAuthData(AuthData data);
}
