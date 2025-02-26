package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDao;

public class AuthService {
    private final AuthDAO authDao = new MemoryAuthDao();

    public void clear() {
        authDao.clear();
    }
}
