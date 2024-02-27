package dataAccess;

import model.*;
import java.util.HashMap;
import java.util.Objects;

public class MemoryUserDAO {
    private HashMap<String, UserInfo> userDataList = new HashMap<String, UserInfo>();

    public UserInfo readUsername(String username){
        return userDataList.get(username);
    }


    public void deleteAllGames() {
        userDataList.clear();
    }
}
