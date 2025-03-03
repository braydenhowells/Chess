package dataaccess;

import model.UserData;

public interface UserDAO {

    public UserData getUser(String username);
    public void createUser(UserData data);
    public void clear();
}

