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

        if (user == null || !login.password().equals(user.password())) {
            throw new DataAccessException("Error: unauthorized");
        }

        return user.username();
    }

    public String add(RegisterRequest request) throws DataAccessException {
        if (request.username() == null || request.password() == null || request.email() == null) {
            throw new DataAccessException("Error: bad request");
        }
        if (userDAO.readUsername(request.username()) != null) {
            throw new DataAccessException("Error: already taken");
        }
        UserInfo newUser = new UserInfo(request.username(), request.password(), request.email());
        userDAO.createUser(newUser);

        return newUser.username();
    }


}
