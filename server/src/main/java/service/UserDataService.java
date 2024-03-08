package service;


import dataAccess.*;
import model.*;
import mySQLdata.UserSQL;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import requests.*;

public class UserDataService {
    private final UserSQL userDAO = new UserSQL();

    public void clear()throws DataAccessException{
        userDAO.deleteAllUsers();
    }

    public String login(LoginRequest login) throws DataAccessException {
        UserInfo user = userDAO.readUser(login.username());

        if (user == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = userDAO.readUser(login.username()).password();

        if (!encoder.matches(login.password(), hashedPassword)) {
            throw new DataAccessException("Error: unauthorized");
        }
        return user.username();
    }

    public String add(RegisterRequest request) throws DataAccessException {
        if (request.username() == null || request.password() == null || request.email() == null) {
            throw new DataAccessException("Error: bad request");
        }
        if (userDAO.readUser(request.username()) != null) {
            throw new DataAccessException("Error: already taken");
        }
        UserInfo newUser = new UserInfo(request.username(), request.password(), request.email());
        userDAO.createUser(newUser);

        return newUser.username();
    }


}
