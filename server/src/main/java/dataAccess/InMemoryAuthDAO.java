package dataAccess;

import model.AuthInfo;

import java.util.HashMap;
import java.util.UUID;

public class InMemoryAuthDAO implements MyAuthDAO{
    private final HashMap<String, AuthInfo> authMap = new HashMap<>();
    public String createAuth(String username){
        UUID uuid = UUID.randomUUID();
        AuthInfo auth = new AuthInfo(username, uuid.toString());
        authMap.put(uuid.toString(), auth);
        return uuid.toString();
    }

    public AuthInfo readAuth(String authToken){
        return authMap.get(authToken);
    }


}