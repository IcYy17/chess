package server;
import com.google.gson.Gson;
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

    private Object logOut(Request req, Response res) {
        return null;
    }
}