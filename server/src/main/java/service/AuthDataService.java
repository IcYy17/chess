package service;

import dataAccess.*;
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
        if(authDAO.readAuth(authToken) != null) {
            authDAO.deleteAuthToken(authToken);
        }
        else {
            throw new DataAccessException("Error: unauthorized");}
    }
    public String getUsername(String authToken) throws DataAccessException{
        if(authDAO.readAuth(authToken) == null){
            throw new DataAccessException("error: unauthorized");
        }
        return authDAO.readAuth(authToken).username();
    }
    public void verifyAuth(String authToken) throws DataAccessException {
        if(authDAO.readAuth(authToken) == null){
            throw new DataAccessException("error: unauthorized");
        }
    }
}
