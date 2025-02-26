package service;

import java.util.UUID;
import dataaccess.*; // * imports all from that package
import model.*;
import requests.RegisterRequest;
import results.RegisterResult;

public class UserService {

    private final UserDAO userDao = new MemoryUserDao();
    private final AuthDAO authDao = new MemoryAuthDao();

    public RegisterResult register(RegisterRequest registerRequest) {
        UserData userdata = userDao.getUser(registerRequest.username());

        if (!(userdata == null)) {
            return new RegisterResult("Error: username is already taken", null, null);
        }

        userDao.createUser(new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email()));

        AuthData authdata = new AuthData(UUID.randomUUID().toString(), registerRequest.username());
        authDao.createAuth(authdata);

        return new RegisterResult(null, registerRequest.username(), authdata.authToken());
    }

    public void clear() {
        userDao.clear();
    }
}
