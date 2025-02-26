package service;

import java.util.UUID;
import dataaccess.*; // * imports all from that package
import model.*;
import requests.RegisterRequest;
import results.RegisterResult;

public class UserService {
    public RegisterResult register(RegisterRequest registerRequest) {
        UserDAO userDao = new MemoryUserDao();
        UserData userdata = userDao.getUser(registerRequest.username());

        AuthDAO authDao = new MemoryAuthDao();



        if (!(userdata == null)) {
            return new RegisterResult("Error: username is already taken", null, null);
        }

        userDao.createUser(new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email()));

        AuthData authdata = new AuthData(UUID.randomUUID().toString(), registerRequest.username());
        authDao.createAuth(authdata);

        return new RegisterResult(null, registerRequest.username(), authdata.authToken());
    }
}
