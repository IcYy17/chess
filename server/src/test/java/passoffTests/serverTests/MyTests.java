package passoffTests.serverTests;
import dataAccess.DataAccessException;
import org.junit.jupiter.api.Test;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.LoginRequest;
import requests.RegisterRequest;
import response.CreateGameResponse;
import service.AuthDataService;
import service.GameDataService;
import service.UserDataService;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
public class MyTests {
    private final GameDataService gamedataService = new GameDataService();
    private final AuthDataService authdataService = new AuthDataService();
    private final UserDataService userdataService = new UserDataService();


    RegisterRequest registerRequest = new RegisterRequest("Isaac", "12345", "isaacnash@gmail.com");
    LoginRequest loginRequest = new LoginRequest("Isaac", "12345");
    CreateGameRequest createGameRequest = new CreateGameRequest("chess game");


    @Test
    public void positiveRegister() throws DataAccessException {
        userdataService.clear();
        userdataService.add(registerRequest);
        authdataService.add(registerRequest);
    }
    @Test
    public void positiveClear() throws DataAccessException {
        userdataService.clear();
        userdataService.add(registerRequest);
        authdataService.add(registerRequest);
        gamedataService.createGame(createGameRequest);

        userdataService.clear();
        authdataService.clear();
        gamedataService.clear();
    }

    @Test
    public void negativeRegister() {

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            RegisterRequest badRequest = new RegisterRequest(null, null, null);
            userdataService.add(badRequest);
            authdataService.add(badRequest);
        });
        assertTrue(exception.getMessage().toLowerCase().contains("bad request"));
    }

    @Test
    public void positiveLogout() throws DataAccessException {
        userdataService.clear();///
        RegisterRequest newUserRequest = registerRequest;
        userdataService.add(newUserRequest);
        String validToken = authdataService.add(newUserRequest);

        authdataService.logout(validToken);
    }

    @Test
    public void positiveLogin() throws DataAccessException{
        userdataService.clear();
        userdataService.add(registerRequest);
        authdataService.add(registerRequest);

        userdataService.login(loginRequest);
        authdataService.login(loginRequest);
    }
    @Test
    public void positiveCreateGame() throws DataAccessException {
        userdataService.clear();
        userdataService.add(registerRequest);
        String sessionToken = authdataService.add(registerRequest);
        authdataService.verifyAuth(sessionToken);

        gamedataService.createGame(createGameRequest);
    }
    @Test
    public void positiveListGames() throws DataAccessException {
        userdataService.clear();
        userdataService.add(registerRequest);
        String userToken = authdataService.add(registerRequest);
        authdataService.verifyAuth(userToken);
        for (int i = 0; i < 3; i++) {
            gamedataService.createGame(createGameRequest);
        }

        gamedataService.listGames();
    }
    @Test
    public void positiveJoinGame() throws DataAccessException {
        userdataService.clear();
        userdataService.add(registerRequest);
        String authToken = authdataService.add(registerRequest);
        authdataService.verifyAuth(authToken);

        CreateGameResponse gameCreationResponse = gamedataService.createGame(createGameRequest);
        int gameId = gameCreationResponse.gameID();


        JoinGameRequest requestToJoinGame = new JoinGameRequest("Isaac", gameId);
        gamedataService.joinGame(requestToJoinGame, "Isaac");
    }
    @Test
    public void negativeJoinGame() {

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userdataService.clear();
            userdataService.add(registerRequest);
            String authToken = authdataService.add(registerRequest);
            authdataService.verifyAuth(authToken);
            gamedataService.createGame(createGameRequest);
            JoinGameRequest joinGameRequest = new JoinGameRequest("Isaac", 234);
            gamedataService.joinGame(joinGameRequest, "Isaac");
        });
        assertTrue(exception.getMessage().toLowerCase().contains("bad request"));
    }
    @Test
    public void negativeListGames() {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userdataService.clear();
            userdataService.add(registerRequest);
            authdataService.add(registerRequest);
            String invalidToken = UUID.randomUUID().toString();
            authdataService.verifyAuth(invalidToken);
        });
        assertTrue(exception.getMessage().toLowerCase().contains("unauthorized"));
    }

    @Test
    public void negativeCreateGame() {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userdataService.clear();
            userdataService.add(registerRequest);
            authdataService.add(registerRequest);
            gamedataService.createGame(new CreateGameRequest(null));
        });
        assertTrue(exception.getMessage().toLowerCase().contains("bad request"));
    }
    @Test
    public void negativeLogin() {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userdataService.clear();
            userdataService.add(registerRequest);
            authdataService.add(registerRequest);
            LoginRequest badLogin = new LoginRequest("asdf", "griddy");
            userdataService.login(badLogin);
        });
        assertTrue(exception.getMessage().toLowerCase().contains("unauthorized"));
    }
    @Test
    public void negativeLogout() {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userdataService.clear();
            userdataService.add(registerRequest);
            authdataService.add(registerRequest);

            String badLogout = UUID.randomUUID().toString();
            authdataService.logout(badLogout);
        });
        assertTrue(exception.getMessage().toLowerCase().contains("unauthorized"));
    }






}

