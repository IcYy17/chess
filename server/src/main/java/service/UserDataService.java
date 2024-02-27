package service;

import dataAccess.DataAccessException;
import dataAccess.MemoryUserDAO;
import model.*;
import requests.*;

public class UserDataService {
    private final MemoryUserDAO userDAO = new MemoryUserDAO();

    public void clear(){
        userDAO.deleteAllGames();
    }

//    public String register(RegistrationRequest request) throws DataAccessException {
//        if(userDAO.readUsername(request.username()) != null){
//            throw new DataAccessException("Error: usename already taken");
//        }
//        if(request.username() == null || request.password() == null || request.email() == null){
//            throw new DataAccessException("Error: not a request");
//        }
//        UserInfo newUser = new UserInfo(request.username(), request.password(), request.email());
//        userDAO.createUser(newUser);
//        return newUser.username();
//    }

}
