package dataAccess;

import model.*;
import java.util.HashMap;
import java.util.Objects;

public class MemoryUserDAO {
    private HashMap<String, UserInfo> userDataList = new HashMap<String, UserInfo>();

    public void clearAllUsers() {
        userDataList.clear();
    }
    public UserInfo readUsername(String username){
        return userDataList.get(username);
    }
    public void createUser(UserInfo user){
        userDataList.put(user.username(),user);
    }

}
