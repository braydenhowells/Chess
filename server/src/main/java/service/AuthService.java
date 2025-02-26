package service;

import dataaccess.AuthDAO;

public class AuthService {
    private final AuthDAO authDao;

    public AuthService(AuthDAO authDao) {
        this.authDao = authDao;
    }

    public void clear() {
        authDao.clear();
    }
}
