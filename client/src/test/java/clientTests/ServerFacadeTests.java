package clientTests;

import org.junit.jupiter.api.*;
import server.Server;
import ui.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);
        server.run(port);
        serverFacade = new ServerFacade("http://localhost:8080");
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterEach
    public void resetDatabase(){
        serverFacade.clearData();
    }

    @AfterAll
    public static void tearDown() {
        serverFacade.clearData();
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }


}
