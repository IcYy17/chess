package dataAccess;

import model.AuthInfo;

public interface AuthDAO {
    String createAuth(String username)throws DataAccessException;
    AuthInfo readAuth(String authToken)throws DataAccessException;
    void deleteAuthToken(String authToken)throws DataAccessException;
    void deleteAuthData() throws DataAccessException;


}
