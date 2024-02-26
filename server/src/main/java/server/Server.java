package server;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import spark.;
import java.util.;

public class Server {
    private ArrayList<String> names = new ArrayList<>();

    public static void main(String[] args) {
        new Server().run();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("resources");
        Spark.awaitInitialization();
        return Spark.port();
    }
    private boolean clearServices(){
        return true;
    }
   private Object clear(Request request,Response response){
        boolean clearedSuccess = clearServices();
        if(clearedSuccess){
            response.status(200);
            return "{}";
        }else{
            response.status(500);
            Map<String,String> error = Map.of("message","Error: unable to clear");
            return new Gson().toJson(error);
        }
   }
    private Object addUser(Request req, Response res) {
        return null;
    }

    private Object listNames(Request req, Response res) {
        return null;
    }

    private Object logout(Request request, Response response) {
        String sessionToken = request.headers("Authorization-Token");
        try {
            userService.endUserSession(sessionToken);
            response.status(200);
            response.type("application/json");
            return new Gson().toJson(Map.of("status", "Session ended successfully"));
        } catch (DataAccessException dae) {
            response.type("application/json");
            response.status(dae.getMessage().contains("Unauthorized") ? 401 : 500);
            return new Gson().toJson(Map.of("error", "Error: " + dae.getMessage()));
        }
    }
}