package service;

import com.google.gson.Gson;
import model.User;
import dataAccess.UserDataAccess;
import spark.Spark;

public class UserDataService {
    private final UserDataAccess userDataAccess = new UserDataAccess();
    private final Gson gson = new Gson();

    public int run(int desiredPort){
        Spark.port(desiredPort);
        Spark.staticFiles.location(/resources/,resources);
        endpoints();

        Spark.awaitInitialization();
        return Spark.port();
    }
}
