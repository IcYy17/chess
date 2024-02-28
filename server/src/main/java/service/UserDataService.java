package service;


import dataAccess.*;
import model.*;
import requests.*;

public class UserDataService {
    private final MemoryUserDAO userDAO = new MemoryUserDAO();

    public void clear(){
        userDAO.clearAllGames();
    }

    public String login(LoginRequest request) throws DataAccessException{
        String password = userDAO.readUsername(request.username()).password();
        if(!request.password().equals(password)){
            throw new DataAccessException("error: incorrect password");
        }
        if(userDAO.readUsername(request.username()) == null){
            throw new DataAccessException("error: unregistered username");
        }

        return userDAO.readUsername(request.username()).username();
    }

    public String add(RegisterRequest request) throws DataAccessException {
        if(userDAO.readUsername(request.username()) != null){
            throw new DataAccessException("Error: username already taken");
        }
        if(request.username() == null || request.password() == null || request.email() == null){
            throw new DataAccessException("Error: not a request");
        }
        UserInfo newUser = new UserInfo(request.username(), request.password(), request.email());
        userDAO.createUser(newUser);
        return newUser.username();
    }


}
