package dataAccess;

import model.AuthInfo;

public interface AuthDAO {
    String createAuth(String username);
    AuthInfo readAuth(String authToken);
    void deleteAuthToken(String authToken);


}
