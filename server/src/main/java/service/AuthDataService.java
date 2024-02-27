package service;

import dataAccess.DataAccessException;
import dataAccess.InMemoryAuthDAO;
import requests.*;
import response.*;

public class AuthDataService {
    private final InMemoryAuthDAO authDAO = new InMemoryAuthDAO();
    public String login(LoginRequest request){
        return authDAO.createAuth(request.username());
    }
}
