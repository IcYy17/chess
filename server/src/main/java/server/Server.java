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
        Spark.delete("/db", this::clear);
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
    private int resStatus(String status) {
        return switch (status) {
            default -> 500;
            case "Error: bad request" -> 400;
            case "Error: unauthorized" -> 401;
            case "Error: not available" -> 403;

        };
    }
    private String errorHandler(DataAccessException exception, Response response){
        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage());
        response.status(resStatus(exception.getMessage()));
        return new Gson().toJson(errorResponse,ErrorResponse.class);
    }

//   private Object clear(spark.Request request, Response response){
//       userDataService.clear();
//       response.status(200);
//       return "{}";
    private Object clear(Request requ, Response res) throws DataAccessException {
        gameDataService.clear();
        userDataService.clear();
        authDataService.clear();
        res.status(200);
        return "{}";
    }
    private Object addUser(Request requ, Response res) throws DataAccessException {
        RegisterRequest user = new Gson().fromJson(requ.body(), RegisterRequest.class);
            String username = userDataService.add(user);
            String authToken = authDataService.add(user);
            RegisterResponse response = new RegisterResponse(username, authToken);
            res.status(200);
            return new Gson().toJson(response, RegisterResponse.class);

    }



    private Object userLogin(Request requ, Response res) throws DataAccessException {
        LoginRequest login = new Gson().fromJson(requ.body(), LoginRequest.class);
            String username = userDataService.login(login);
            String authToken = authDataService.login(login);
            LoginResponse response = new LoginResponse(username,authToken);
            res.status(200);
            return new Gson().toJson(response, LoginResponse.class);

    }

    private Object listGames(Request requ, Response res){
        try{
        String authToken = requ.headers("authorization");
            authDataService.verify(authToken);
            ListGamesResponse games = gameDataService.listGames();
            res.status(200);
            return new Gson().toJson(games, ListGamesResponse.class);}
        catch(DataAccessException exception){
            return errorHandler(exception, res);

        }
    }

    private Object joinGame(Request requ, Response res){

        JoinGameRequest request = new Gson().fromJson(requ.body(), JoinGameRequest.class);
        String authToken = requ.headers("authorization");
        try{
            String username = authDataService.getUsername(authToken);
            gameDataService.joinGame(request, username);
            res.status(200);
            return "{}";
        }
        catch(DataAccessException exception){
            return errorHandler(exception, res);

        }
    }

    private Object createGame(Request requ, Response res) throws DataAccessException {

        CreateGameRequest request = new Gson().fromJson(requ.body(), CreateGameRequest.class);

        String authToken = requ.headers("authorization");
            authDataService.verify(authToken);
            CreateGameResponse response = gameDataService.createGame(request);
            res.status(200);
            return new Gson().toJson(response, CreateGameResponse.class);

    }

    private Object logout(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("authorization");
        authDataService.logout(authToken);
        res.status(200);
        return "{}";
    }



    public void stop(){
        Spark.stop();
        Spark.awaitStop();
    }
}