package dataAccess;

import model.AuthInfo;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    private final HashMap<String, AuthInfo> authDataMap = new HashMap<>();
    public String createAuth(String username){
        UUID uuid = UUID.randomUUID();
        AuthInfo auth = new AuthInfo(username, uuid.toString());
        authDataMap.put(uuid.toString(), auth);
        return uuid.toString();
    }

    public AuthInfo readAuth(String authToken){
        return authDataMap.get(authToken);

    }

    public void deleteAuthData() {
        authDataMap.clear();
    }
    public void deleteAuthToken(String authToken) {
        authDataMap.remove(authToken);
    }
}