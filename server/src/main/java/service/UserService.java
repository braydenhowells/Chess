package service;

import java.util.UUID;
import dataaccess.*; // * imports all from that package
import model.*;
import requests.LoginRequest;
import requests.LogoutRequest;
import requests.RegisterRequest;
import results.LoginResult;
import results.SimpleResult;

public class UserService {

    private final UserDAO userDao;
    private final AuthDAO authDao;

    public UserService(UserDAO userDao, AuthDAO authDao) {
        this.userDao = userDao;
        this.authDao = authDao;
    }

    public LoginResult register(RegisterRequest registerRequest) {
        UserData userdata = userDao.getUser(registerRequest.username());

        if (!(userdata == null)) {
            return new LoginResult("Error: username is already taken", null, null);
        }

        userDao.createUser(new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email()));

        AuthData authdata = new AuthData(UUID.randomUUID().toString(), registerRequest.username());
        authDao.createAuth(authdata);

        return new LoginResult(null, registerRequest.username(), authdata.authToken());
    }

    public LoginResult login(LoginRequest loginRequest) {
        UserData userData = userDao.getUser(loginRequest.username());

        if (userData == null) { // username not in db
            return new LoginResult("Error: unauthorized", null, null);
        }

        if (!userData.password().equals(loginRequest.password())) { // password incorrect
            return new LoginResult("Error: unauthorized", null, null);
        }

        // we know that the username exists and that the password matches. time to make an authToken
        AuthData authdata = new AuthData(UUID.randomUUID().toString(), loginRequest.username());
        authDao.createAuth(authdata);
        return new LoginResult(null, loginRequest.username(), authdata.authToken());
    }

    public SimpleResult logout(LogoutRequest lreq) {
        AuthData authData = authDao.findAuthData(lreq.authToken());
        if (authData == null) {
            return new SimpleResult("Error: unauthorized");
        }
        authDao.deleteAuthData(authData);
        return new SimpleResult(null);
    }

    public void userClear() {
        userDao.clear();
    }

    public void authClear() {
        authDao.clear();
    }

    public AuthData getAuthData(String authToken) {
        return authDao.findAuthData(authToken);
    }
}
