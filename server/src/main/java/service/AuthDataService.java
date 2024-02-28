package service;

import dataAccess.MemoryAuthDAO;
import requests.*;

public class AuthDataService {
    private final MemoryAuthDAO authDAO = new MemoryAuthDAO();
    public String login(LoginRequest request){
        return authDAO.createAuth(request.username());
    }
}
