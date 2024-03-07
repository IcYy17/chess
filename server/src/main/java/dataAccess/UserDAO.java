package dataAccess;


import model.*;
import mySQLdata.UserSQL;

public interface UserDAO {

    void createUser(UserInfo user) throws DataAccessException;
    void deleteUsers() throws DataAccessException;
}
