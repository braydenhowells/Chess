package service;

import java.util.UUID;
import dataaccess.*; // * imports all from that package
import model.*;
import org.mindrot.jbcrypt.BCrypt;
import requests.LoginRequest;
import requests.RegisterRequest;
import results.LoginResult;
import results.SimpleResult;

public class UserService {

    private final UserDAO userDao;
    private final AuthDAO authDao;
    private final AuthService authService;

    public UserService(UserDAO userDao, AuthDAO authDao, AuthService authService) {
        this.userDao = userDao;
        this.authDao = authDao;
        this.authService = authService;
    }

    public LoginResult register(RegisterRequest rreq) {
        UserData userdata = userDao.getUser(rreq.username());

        if (!(userdata == null)) {
            return new LoginResult("Error: username is already taken", null, null);
        }

        String hashedPassword = BCrypt.hashpw(rreq.password(), BCrypt.gensalt());
        userDao.createUser(new UserData(rreq.username(), hashedPassword, rreq.email()));

        AuthData authdata = new AuthData(UUID.randomUUID().toString(), rreq.username());
        authDao.createAuth(authdata);

        return new LoginResult(null, rreq.username(), authdata.authToken());
    }

    public LoginResult login(LoginRequest lreq) {
        UserData userData = userDao.getUser(lreq.username());

        if (userData == null) { // username not in db
            return new LoginResult("Error: unauthorized", null, null);
        }

        if (!BCrypt.checkpw(lreq.password(), userData.password())) { // password incorrect
            return new LoginResult("Error: unauthorized", null, null);
        }

        // we know that the username exists and that the password matches. time to make an authToken
        AuthData authdata = new AuthData(UUID.randomUUID().toString(), lreq.username());
        authDao.createAuth(authdata);
        return new LoginResult(null, lreq.username(), authdata.authToken());
    }

    public SimpleResult logout(String authToken) {
        String verification = authService.verifyAuth(authToken,false, null);
        // bad path
        if (verification.contains("Error")) {
            return new SimpleResult(verification);
        }
        // good path
        if (verification.contains("verified")) {
            // now we know that the token is legit. let's proceed
            AuthData authData = authDao.findAuthData(authToken);
            if (authData == null) {
                return new SimpleResult("Error: unauthorized");
            }
            authDao.deleteAuthData(authData);
            return new SimpleResult(null);
        }
        // wacky path
        else {
            return new SimpleResult("Error: an unexpected error occurred");
        }


    }

    public void userClear() {
        userDao.clear();
    }

}
