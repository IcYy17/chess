package dataAccess;

import model.User;

public interface IUserDAO extends MyDataAccess {
    void createUser(User user) throws DataAccessException;
    User getUser(String username) throws DataAccessException;
}
