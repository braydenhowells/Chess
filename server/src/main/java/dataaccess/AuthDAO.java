package dataaccess;

import model.AuthData;

import java.sql.SQLException;
import java.util.ArrayList;

public interface AuthDAO {
    void createAuth(AuthData data) throws SQLException;
    void clear() throws SQLException;
    AuthData findAuthData(String authToken) throws SQLException;
    void deleteAuthData(AuthData data) throws SQLException;
    ArrayList<AuthData> getAllAuth() throws SQLException;
}
