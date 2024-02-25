package service;

import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.MemoryUserDAO;
import model.MyAuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.HashMap;

public class UserDataService {
    private final UserDataAccess userDataAccess = new UserDataAccess();
    private final Gson gson = new Gson();

    public int run(int desiredPort){
        Spark.port(desiredPort);
        Spark.staticFiles.location(/resources);
        endpoints();

        Spark.awaitInitialization();
        return Spark.port();
    }
    public void endpoints(){
        Spark.post("/user",(request, response) -> {
            boolean success = userDataService.registerUser(user);
            User user = gson.fromJson(request.body(),User.class);

        })
    }
}
