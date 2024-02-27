package dataAccess;

import model.*;
import java.util.HashMap;
import java.util.Objects;

public class MemoryUserDAO {
    private HashMap<String, UserInfo> userDataList = new HashMap<String, UserInfo>();

    public void clearAllGames() {
        userDataList.clear();
    }
    public UserInfo readUser(String username){
        return userDataList.get(username);
    }
    public void createUser(UserInfo user){
        userDataList.put(user.username(),user);
    }
}
