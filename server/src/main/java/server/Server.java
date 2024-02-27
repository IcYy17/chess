package server;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import requests.LoginRequest;
import requests.RegistrationRequest;
import service.UserDataService;
import spark.*;
import java.util.*;

public class Server {
    private ArrayList<String> names = new ArrayList<>();
    private final UserDataService userDataService = new UserDataService();


    public int run(int desiredPort) {

        Spark.port(desiredPort);
        Spark.staticFiles.location("web");
        Spark.delete("/db", this::clearHandler);
        Spark.get("/game", this::listGames);
        Spark.put("/game",this::joinGame);
        Spark.post("/user", this::addUser);
        Spark.post("/session", this::loginUser);
        Spark.post("/game", this::createGame);
        Spark.delete("/session", this::logout);

        Spark.init();
        Spark.awaitInitialization();
        return Spark.port();
    }


   private Object clearHandler(spark.Request request, Response response){
       userDataService.clear();
       return "{}";
   }

    private Object addUser(Request request, Response response) throws DataAccessException {
        return "";
    }

    private Object loginUser(Request request, Response response) throws DataAccessException {
        return 0;
    }

    private Object listGames(Request request, Response response) throws DataAccessException {
        return 0;
    }

    private Object joinGame(Request request, Response response) throws DataAccessException {
        return 0;
    }

    private Object createGame(Request request, Response response) throws DataAccessException {
        return 0;
    }

    private Object logout(Request request, Response response) throws DataAccessException {
        return 0;
    }



    public void stop(){
        Spark.stop();
        Spark.awaitStop();
    }
}