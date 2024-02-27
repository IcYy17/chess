package dataAccess;

import model.AuthInfo;

public interface MyAuthDAO {
    String createAuth(String username);
    AuthInfo readAuth(String authToken);

}
