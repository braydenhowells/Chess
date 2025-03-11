package service;

import dataaccess.AuthDAO;
import model.AuthData;

import java.util.List;

public class AuthService {
    private final AuthDAO authDao;

    public AuthService(AuthDAO authDAO) {
        this.authDao = authDAO;
    }

    public String verifyAuth(String authToken, boolean checkBody, List<String> bodyParts) {
        // check for empty token, 400
        if (authToken == null || authToken.isEmpty()) {
            return "Error: bad request";
        }
        // check to see if token is wrong, 401
        AuthData data = getAuthData(authToken);
        if (data == null) {
            return "Error: unauthorized";
        }
        // see if any the api body pieces are null/empty, 400
        if (checkBody && bodyParts != null) { // check list != null just in case
            for (String bodyPart : bodyParts) {
                if (bodyPart == null || bodyPart.isEmpty()) {
                    return "Error: bad request";
                }
            }
        }
        // if we made it this far, we are golden
        return "verified";
    }

    public AuthData getAuthData(String authToken) {
        return authDao.findAuthData(authToken);
    }

    public void authClear() {
        authDao.clear();
    }

}
