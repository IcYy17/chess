package clientTests;

import exception.ResponseException;
import model.AuthInfo;
import org.junit.jupiter.api.*;
import server.Server;
import ui.*;


public class ServerFacadeTests {
    private static ServerFacade serverFacade;
    private static Server server;


    @BeforeAll
    public static void init() {
        var port = 1234;
        server = new Server();
        server.run(port);
        serverFacade = new ServerFacade("http://localhost:1234");
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterEach
    public void resetData(){
        serverFacade.clearData();
    }

    @AfterAll
    public static void clearServer() {
        serverFacade.clearData();
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void positiveRegister() {
        String username = "newUser";
        String password = "newPass123";
        String email = "newEmail@example.com";

        AuthInfo authResult = null;

        try {
            authResult = serverFacade.register(username, password, email);
        } catch (ResponseException ex) {
            Assertions.fail("Registration failed unexpectedly: " + ex.getMessage());
        }
        Assertions.assertNotNull(authResult, "AuthInfo should not be null after successful registration.");
    }
    @Test
    public void negativeRegister()  {
        String username = "newUser";
        String password = "newPass123";
        String email = "newEmail@example.com";


        try {
            AuthInfo firstAuth = serverFacade.register(username,password, email);
            Assertions.assertNotNull(firstAuth, "First registration should succeed.");
            serverFacade.logout(firstAuth.authToken());
            serverFacade.register(username, password, email);
            Assertions.fail("Second registration attempt should have thrown an exception.");

        } catch (ResponseException exception) {

            Assertions.assertEquals(403, exception.StatusCode(), "Expected a 403 status code for duplicate registration.");
        }
    }
    @Test
    public void positiveLogin()  {
        String username = "newUser";
        String password = "newPass123";
        String email = "newEmail@example.com";

        try {
            AuthInfo registrationResult = serverFacade.register(username, password,email);
            Assertions.assertNotNull(registrationResult, "Registration should be successful.");

            serverFacade.logout(registrationResult.authToken());

            AuthInfo loginResult = serverFacade.login(username, password);
            Assertions.assertNotNull(loginResult, "Login should be successful and return valid AuthInfo.");
        } catch (ResponseException exception) {
            Assertions.fail("An error occurred during test execution: " + exception.getMessage());
        }
    }
    @Test
    public void negativeLogin() throws ResponseException {
        String username = "newUser";
        String password = "newPass123";
        String email = "newEmail@example.com";
        String badPassword = "wrongPassword";

        AuthInfo authData = serverFacade.register(username, password, email);
        Assertions.assertNotNull(authData, "Registration should succeed, but it didn't.");

        serverFacade.logout(authData.authToken());

        ResponseException thrown = Assertions.assertThrows(ResponseException.class, () -> {
            serverFacade.login(username, badPassword);
        }, "Expected login to fail with wrong password, but it succeeded.");

        Assertions.assertEquals(401, thrown.StatusCode(), "Expected 401 status code for failed login attempt.");
    }

    @Test
    public void positiveLogout() throws ResponseException {
        String username = "newUser";
        String password = "newPass123";
        String email = "newEmail@example.com";

        AuthInfo authInfo = serverFacade.register(username, password, email);
        Assertions.assertNotNull(authInfo, "Registration should not return null.");

        serverFacade.login(username, password);
        Assertions.assertNotNull(authInfo, "Login should succeed after registration.");

    }

    @Test
    public void negativeLogout() throws ResponseException {
        String username = "newUser";
        String password = "newPass123";
        String email = "newEmail@example.com";

        AuthInfo authInfo = serverFacade.register(username, password, email);
        Assertions.assertNotNull(authInfo, "Registration should be successful.");

        AuthInfo loginAuthInfo = serverFacade.login(username, password);
        Assertions.assertNotNull(loginAuthInfo, "Login should be successful.");
        Assertions.assertEquals(authInfo.username(), loginAuthInfo.username(), "Logged in username should match.");

    }

    @Test
    public void positiveListGames() throws ResponseException {
        String username = "newUser";
        String password = "newPass123";
        String email = "newEmail@example.com";

        AuthInfo authData = serverFacade.register(username, password, email);
        Assertions.assertNotNull(authData, "Registration failed unexpectedly.");

        try {
            serverFacade.createGame(authData.authToken(), "testGame1");
            serverFacade.createGame(authData.authToken(), "testGame2");
            serverFacade.createGame(authData.authToken(), "testGame3");
            var gamesList = serverFacade.listGames(authData.authToken());
            Assertions.assertNotNull(gamesList, "Game listing should not be null.");
        } catch (ResponseException e) {
            Assertions.fail("Exception thrown: " + e.getMessage());
        }


    }
    @Test
    public void negativeListGames() throws ResponseException {
        String username = "newUser";
        String password = "newPass123";
        String email = "newEmail@example.com";

        try {
            AuthInfo authData = serverFacade.register(username, password, email);
            Assertions.assertNotNull(authData, "Registration should succeed and return auth data.");

            serverFacade.createGame(authData.authToken(), "testGame1");
            serverFacade.createGame(authData.authToken(), "testGame2");
            serverFacade.createGame(authData.authToken(), "testGame3");

            // Attempt to list games with an incorrect authentication token
            serverFacade.listGames("incorrectAuthToken");
            Assertions.fail("Listing games with an incorrect token should throw a ResponseException.");
        } catch (ResponseException e) {
            Assertions.assertEquals(401, e.StatusCode(), "Expected 401 Unauthorized status code for incorrect auth token.");
        }
    }
    @Test
    public void positiveCreateGame() {

        String username = "newUser";
        String password = "newPass123";
        String email = "newEmail@example.com";
        AuthInfo authData;

        try {
            authData = serverFacade.register(username, password, email);
            serverFacade.createGame(authData.authToken(), "testGame1");
            var games = serverFacade.listGames(authData.authToken());

            Assertions.assertNotNull(authData, "Registration should succeed and return non-null authData.");
            Assertions.assertNotNull(games, "Listing games should return non-null list of games.");
        } catch (ResponseException e) {
            Assertions.fail("Unexpected exception: " + e.getMessage());
        }
    }


    @Test
    public void testCreateGameFail() {
        String username = "newUser";
        String password = "newPass123";
        String email = "newEmail@example.com";

        try {
            AuthInfo authData = serverFacade.register(username, password, email);
            Assertions.assertNotNull(authData, "Registration should succeed and return non-null authData.");

            serverFacade.createGame("Wrong Token", "testGame1");

            Assertions.fail("Creating a game with an invalid token should have thrown a ResponseException.");
        } catch (ResponseException e) {
            Assertions.assertEquals(401, e.StatusCode(), "Expected a 401 Unauthorized status code.");
        }
    }



}
