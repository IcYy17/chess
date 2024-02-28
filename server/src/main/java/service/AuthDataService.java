package service;

import dataAccess.MemoryAuthDAO;
import requests.*;

public class AuthDataService {
    private final MemoryAuthDAO authDAO = new MemoryAuthDAO();
    public String login(LoginRequest login){
        return authDAO.createAuth(login.username());
    }

    public String add(RegisterRequest user) {
        return authDAO.createAuth(user.username());
    }

}
