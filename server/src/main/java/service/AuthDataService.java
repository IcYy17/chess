package service;

import dataAccess.*;
import model.AuthInfo;
import requests.*;

public class AuthDataService {
    private final MemoryAuthDAO authDAO = new MemoryAuthDAO();
    public String login(LoginRequest login){
        return authDAO.createAuth(login.username());
    }

    public String add(RegisterRequest user) {
        return authDAO.createAuth(user.username());
    }

    public void clear(){
        authDAO.deleteAuthData();
    }

    public void logout(String authToken) throws DataAccessException {
        if (authDAO.readAuth(authToken) == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        authDAO.deleteAuthToken(authToken);
    }
    public String getUsername(String authToken) throws DataAccessException {
        AuthInfo authInfo = authDAO.readAuth(authToken);
        if (authInfo == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        return authInfo.username();
    }
    public void verifyAuth(String authToken) throws DataAccessException {
        if (authDAO.readAuth(authToken) == null) {
            throw new DataAccessException("Error: unauthorized");
        }
    }
}
