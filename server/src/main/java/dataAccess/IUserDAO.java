package dataAccess;


import model.*;

public interface IUserDAO {

    UserInfo createUser(String username, String password, String email) throws DataAccessException;
    UserInfo getUser(String username) throws DataAccessException;
    public void checkUsername(String username) throws DataAccessException;
    public void checkPassword(String username, String password) throws DataAccessException;
}
