package server;


import com.google.gson.Gson;
import model.User;
import service.UserDataService;
import spark.Spark;

public class Server {


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        endpoints();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void endpoints() {
        Spark.post("/user", (request, response) -> {
            return "User registration endpoint";
        });

        Spark.post("/session", (request, response) -> {
            return "User login endpoint";
        });

    }
}
