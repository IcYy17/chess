package server;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import requests.*;
import response.*;
import service.AuthDataService;
import service.GameDataService;
import service.UserDataService;
import spark.*;

public class Server {
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

            case "Error: bad request" -> 400;
            case "Error: unauthorized" -> 401;
            case "Error: already taken" -> 403;
            default -> 500;


        };
    }
    private String errorHandler(DataAccessException exception, Response res) {
        int status = resStatus(exception.getMessage());
        res.status(status);
        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage());
        return new Gson().toJson(errorResponse);
    }

    private Object clear(Request requ, Response res){
        try {
            gameDataService.clear();
            userDataService.clear();
            authDataService.clear();
            res.status(200);
            return "{}";
        }catch(DataAccessException ex){
            return errorHandler(ex, res);

        }
    }
    private Object addUser(Request requ, Response res) {
        try {
            RegisterRequest registrationDetails = new Gson().fromJson(requ.body(), RegisterRequest.class);
            String newUsername = userDataService.add(registrationDetails);
            String newAuthToken = authDataService.add(registrationDetails);
            res.status(200);
            return new Gson().toJson(new RegisterResponse(newUsername, newAuthToken));
        } catch (DataAccessException exception) {
            return errorHandler(exception, res);
        }
    }



    private Object userLogin(Request requ, Response res) {
        try {
            LoginRequest loginDetails = new Gson().fromJson(requ.body(), LoginRequest.class);
            String obtainedUsername = userDataService.login(loginDetails);
            String authToken = authDataService.login(loginDetails);
            res.status(200);
            return new Gson().toJson(new LoginResponse(obtainedUsername, authToken));
        } catch (DataAccessException exception) {
            return errorHandler(exception, res);
        }
    }

    private Object listGames(Request requ, Response res) {
        try {
            String authToken = requ.headers("authorization");
            authDataService.verifyAuth(authToken);
            ListGamesResponse gamesResponse = gameDataService.listGames();
            res.status(200);
            return new Gson().toJson(gamesResponse);
        } catch (DataAccessException exception) {
            return errorHandler(exception, res);
        }
    }

    private Object joinGame(Request requ, Response res){
        try{
            JoinGameRequest request = new Gson().fromJson(requ.body(), JoinGameRequest.class);
            String authToken = requ.headers("authorization");

            String username = authDataService.getUsername(authToken);
            gameDataService.joinGame(request, username);
            res.status(200);
            return "{}";
        }
        catch(DataAccessException exception){
            return errorHandler(exception, res);

        }
    }

    private Object createGame(Request requ, Response res) {
        try {
            CreateGameRequest request = new Gson().fromJson(requ.body(), CreateGameRequest.class);
            String authToken = requ.headers("authorization");
            authDataService.verifyAuth(authToken);
            CreateGameResponse gameResponse = gameDataService.createGame(request);
            res.status(200);
            return new Gson().toJson(gameResponse, CreateGameResponse.class);
    }   catch(DataAccessException exception) {
            return errorHandler(exception, res);
        }

    }

    private Object logout(Request requ, Response res) {
        try {
            String authToken = requ.headers("authorization");
            authDataService.logout(authToken);
            res.status(200);
            return "{}";
        }
        catch(DataAccessException exception) {
            return errorHandler(exception, res);
        }
    }

    public void stop(){
        Spark.stop();
        Spark.awaitStop();
    }
}