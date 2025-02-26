package dataaccess;

import model.UserData;

public interface UserDAO {

    // CRUD
    // this is the 'read' part of CRUD
    public UserData getUser(String username);
    public void createUser(UserData data);
    public void clear();
}

