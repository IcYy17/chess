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
        String username = userdataService.add(registerRequest);
        String authToken = authdataService.add(registerRequest);
    }

    @Test
    public void negativeRegister() {

        RegisterRequest invalidRegistrationDetails = new RegisterRequest("", "", "");

        DataAccessException thrownException = assertThrows(DataAccessException.class, () -> {
            userdataService.add(invalidRegistrationDetails);
            authdataService.add(invalidRegistrationDetails);
        }, "Expected register to throw, but it didn't");

        String expectedMessage = "bad request";
        String actualMessage = thrownException.getMessage().toLowerCase();
        assertTrue(actualMessage.contains(expectedMessage),
                String.format("Exception message was expected to contain '%s', but was '%s'", expectedMessage, actualMessage));
    }

    @Test
    public void positiveLogout() throws DataAccessException {
        RegisterRequest newUserRequest = registerRequest;
        userdataService.add(newUserRequest);
        String validToken = authdataService.add(newUserRequest);

        authdataService.logout(validToken);


    }

    @Test
    public void positiveLogin() throws DataAccessException{
        String user = userdataService.add(registerRequest);
        String token = authdataService.add(registerRequest);

        userdataService.login(loginRequest);
        authdataService.login(loginRequest);
    }
    @Test
    public void successfulGameCreation() throws DataAccessException {
        userdataService.add(registerRequest);
        String sessionToken = authdataService.add(registerRequest);
        authdataService.verifyAuth(sessionToken);

        gamedataService.createGame(createGameRequest);
    }
    @Test
    public void successfullyListsGames() throws DataAccessException {
        userdataService.add(registerRequest);
        String userToken = authdataService.add(registerRequest);
        authdataService.verifyAuth(userToken);
        for (int i = 0; i < 3; i++) {
            gamedataService.createGame(createGameRequest);
        }

        gamedataService.listGames();
    }
    @Test
    public void successfulGameJoining() throws DataAccessException {
        userdataService.add(registerRequest);
        String authToken = authdataService.add(registerRequest);
        authdataService.verifyAuth(authToken);

        CreateGameResponse gameCreationResponse = gamedataService.createGame(createGameRequest);
        int gameId = gameCreationResponse.gameID();


        JoinGameRequest requestToJoinGame = new JoinGameRequest("Isaac", gameId);
        gamedataService.joinGame(requestToJoinGame, "Isaac");
    }






}

