package service;

import model.User;
import dataAccess.UserDataAccess;

public class UserDataService {
    private final UserDataAccess userDataAccess = new UserDataAccess();

    public boolean registerUser(User user) {
        if (userDataAccess.getUserByUsername(user.username()) != null) {

            return false;
        }
        userDataAccess.createUser(user);
        return true;
    }
}
