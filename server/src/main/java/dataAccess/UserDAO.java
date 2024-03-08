package dataAccess;


import model.*;
import mySQLdata.UserSQL;

public interface UserDAO {

    void createUser(UserInfo user) throws DataAccessException;
    void deleteAllUsers() throws DataAccessException;
    UserInfo readUser(String Username) throws DataAccessException;
}
