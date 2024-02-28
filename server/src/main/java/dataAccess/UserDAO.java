package dataAccess;


import model.*;

public interface UserDAO {

    void createUser(UserInfo user);
    UserInfo readUser(String username);
    void deleteAllGames();
}
