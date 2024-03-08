package service;

import dataAccess.*;
import model.AuthInfo;
import mySQLdata.AuthSQL;
import requests.*;

public class AuthDataService {
    private final AuthSQL authDAO = new AuthSQL();
    public String login(LoginRequest login)throws DataAccessException{
        return authDAO.createAuth(login.username());
    }

    public String add(RegisterRequest user)throws DataAccessException {
        return authDAO.createAuth(user.username());
    }

    public void clear()throws DataAccessException{
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
