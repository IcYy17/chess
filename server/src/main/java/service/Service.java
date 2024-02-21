package service;

import model.User;
import dataAccess.UserDataAccess;

public class UserDataService {
    private UserDataAccess userDataAccess;

    public UserDataService() {
        this.userDataAccess = new UserDataAccess();
    }

    public void registerUser(User user) {

        userDataAccess.createUser(user);
    }

}
