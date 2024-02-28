package server;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import requests.*;
import response.*;
import service.AuthDataService;
import service.GameDataService;
import service.UserDataService;
import spark.*;
import java.util.*;

public class Server {
    private ArrayList<String> names = new ArrayList<>();
    private final UserDataService userDataService = new UserDataService();
    private final AuthDataService authDataService = new AuthDataService();
    private final GameDataService gameDataService = new GameDataService();


    public int run(int desiredPort) {

        Spark.port(desiredPort);
        Spark.staticFiles.location("web");
        Spark.delete("/db", this::clearAll);
        Spark.get("/game", this::listGames);
        Spark.put("/game",this::joinGame);
        Spark.post("/user", this::addUser);
        Spark.post("/session", this::userLogin);
        Spark.post("/game", this::createGame);
        Spark.delete("/session", this::logout);

        Spark.init();
        Spark.awaitInitialization();
        return Spark.port();
    }


   private Object clearAll(spark.Request request, Response response){
       userDataService.clear();
       response.status(200);
       return "{}";
   }

    private Object addUser(Request req, Response res) throws DataAccessException {
        RegisterRequest user = new Gson().fromJson(req.body(), RegisterRequest.class);
            String username = userDataService.add(user);
            String authToken = authDataService.add(user);
            RegisterResponse response = new RegisterResponse(username, authToken);
            res.status(200);
            return new Gson().toJson(response, RegisterResponse.class);

    }



    private Object userLogin(Request req, Response res) throws DataAccessException {
        LoginRequest login = new Gson().fromJson(req.body(), LoginRequest.class);
            String username = userDataService.login(login);
            String authToken = authDataService.login(login);
            LoginResponse response = new LoginResponse(username,authToken);
            res.status(200);
            return new Gson().toJson(response, LoginResponse.class);

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