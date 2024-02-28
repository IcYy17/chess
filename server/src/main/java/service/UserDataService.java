package service;


import dataAccess.*;
import model.*;
import requests.*;

public class UserDataService {
    private final MemoryUserDAO userDAO = new MemoryUserDAO();

    public void clear(){
        userDAO.clearAllUsers();
    }

    public String login(LoginRequest login) throws DataAccessException {
        UserInfo user = userDAO.readUsername(login.username());

        if (user == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        String password = userDAO.readUsername(login.username()).password();

        if (!login.password().equals(password)) {
            throw new DataAccessException("Error: unauthorized");
        }

        return user.username();
    }

    public String add(RegisterRequest request) throws DataAccessException {
        if(userDAO.readUsername(request.username()) != null){
            throw new DataAccessException("Error: unauthorized");
        }
        if(request.username() == null || request.password() == null || request.email() == null){
            throw new DataAccessException("Error: unauthorized");
        }
        UserInfo newUser = new UserInfo(request.username(), request.password(), request.email());
        userDAO.createUser(newUser);
        return newUser.username();
    }


}
