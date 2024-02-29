package dataAccess;


import model.*;

public interface UserDAO {

    void createUser(UserInfo user);
    void deleteAllGames();
}
