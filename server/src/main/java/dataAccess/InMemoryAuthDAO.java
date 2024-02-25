package dataAccess;

import model.AuthToken;
import java.util.HashMap;
import java.util.Map;

public class InMemoryAuthDAO implements IAuthDAO {
    private Map<String, AuthToken> authTokens = new HashMap<>();


}