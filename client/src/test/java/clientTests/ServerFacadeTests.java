package clientTests;

import exception.ResponseException;
import model.AuthInfo;
import org.junit.jupiter.api.*;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.RegisterRequest;
import response.CreateGameResponse;
import response.ListGamesResponse;
import response.RegisterResponse;
import server.Server;
import ui.*;

import static org.junit.jupiter.api.Assertions.assertEquals;


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
        String email = null;


        try {
            serverFacade.register(username, password, email);
            Assertions.fail("Expected exception");
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
            Assertions.assertEquals("Error\n", e.getMessage());
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
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
            Assertions.assertEquals("Error\n", e.getMessage());
        }
    }
    @Test
    public void negativeLogin() {
        String username = "newUser";
        String password = "newPass123";
        String email = "newEmail@example.com";
        String wrongPassword = "wrongPassword";

        try {
            AuthInfo authData = serverFacade.register(username, password, email);
            Assertions.assertNotNull(authData, "Registration should succeed and return non-null AuthInfo.");
            serverFacade.logout(authData.authToken());

            serverFacade.login(username, wrongPassword);

            Assertions.fail("Login with an incorrect password should have thrown a ResponseException.");
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
            Assertions.assertEquals("Error\n", e.getMessage());
        }
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
    public void negativeLogout() {
        String username = "newUser";
        String password = "newPass123";
        String email = "newEmail@example.com";

        try {
            AuthInfo authData = serverFacade.register(username, password, email);
            Assertions.assertNotNull(authData);
            serverFacade.logout("wrongToken");
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
            Assertions.assertEquals("Error\n", e.getMessage());
        }
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
    public void negativeListGames()  {
        String username = "newUser";
        String password = "newPass123";
        String email = "newEmail@example.com";

        try {
            AuthInfo authData = serverFacade.register(username, password, email);
            Assertions.assertNotNull(authData, "Registration should succeed and return auth data.");

            serverFacade.createGame(authData.authToken(), "test1");
            serverFacade.createGame(authData.authToken(), "test2");
            serverFacade.createGame(authData.authToken(), "test3");

            serverFacade.listGames("incorrectAuthToken");
            Assertions.fail("Expected exception");
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
            Assertions.assertEquals("Error\n", e.getMessage());
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
            serverFacade.createGame(authData.authToken(), "test1");
            var games = serverFacade.listGames(authData.authToken());

            Assertions.assertNotNull(authData, "Registration should succeed and return non-null authData.");
            Assertions.assertNotNull(games, "Listing games should return non-null list of games.");
        } catch (ResponseException e) {
            Assertions.fail("Unexpected exception: " + e.getMessage());
        }
    }


    @Test
    public void negativeCreateGame() {
        String username = "newUser";
        String password = "newPass123";
        String email = "newEmail@example.com";
        AuthInfo authData;
        try {
            authData = serverFacade.register(username, password, email);
            Assertions.assertNotNull(authData, "Registration should succeed and return non-null authData.");

            serverFacade.createGame("bad", "test1");

            Assertions.fail("Creating a game with an invalid token should have thrown a ResponseException.");
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
            Assertions.assertEquals("Error\n", e.getMessage());
        }
    }
    @Test
    public void joinGamePositive() {
        String username = "newUser";
        String password = "newPass123";
        String email = "newEmail@example.com";


        try {
            AuthInfo authData = serverFacade.register(username, password, email);
            Assertions.assertNotNull(authData);
            serverFacade.createGame(authData.authToken(), "testGame1");
            var games = serverFacade.listGames(authData.authToken());
            Assertions.assertNotNull(games);
            var result = serverFacade.joinGame(authData.authToken(), games.games().get(0).gameID(), "white");
            Assertions.assertNotNull(result);
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
            Assertions.assertEquals("Error\n", e.getMessage());
        }
    }
    @Test
    public void joinGameNegative() {
        String username = "newUser";
        String password = "newPass123";
        String email = "newEmail@example.com";

        try {
            AuthInfo authData = serverFacade.register(username, password, email);
            Assertions.assertNotNull(authData, "AuthData should not be null after registration.");

            serverFacade.createGame(authData.authToken(), "testGame1");
            var games = serverFacade.listGames(authData.authToken());
            Assertions.assertNotNull(games, "Games list should not be null after creating a game.");
            int invalidGameId = -1;
            serverFacade.joinGame(authData.authToken(), invalidGameId, "white");
            Assertions.fail("Joining a game with an invalid game ID should have thrown a ResponseException.");
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
            Assertions.assertEquals("Error\n", e.getMessage());
        }
    }





}
