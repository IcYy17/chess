package dataAccessTests;
import chess.ChessGame;
import dataAccess.DataAccessException;
import mySQLdata.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class DataAccessTests {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final UserSQL userDAO = new UserSQL();
    private final AuthSQL authDAO = new AuthSQL();
    private final GameSQL gameDAO = new GameSQL();

    UserInfo userInfo = new UserInfo("test","qwer","1234@m.com");
    UserInfo hashed = new UserInfo("test",encoder.encode("qwer"),"1234@m.com");
    GameInfo game1 = new GameInfo(1,"white","black","chess1",new ChessGame());
    GameInfo game2 = new GameInfo(2,"white","black","chess2",new ChessGame());
    GameInfo game3 = new GameInfo(3,"white","black","chess3",new ChessGame());

    @BeforeEach
    public void start() throws DataAccessException{
        userDAO.deleteAllUsers();
        gameDAO.deleteAllGames();
        authDAO.deleteAuthData();
    }
    @Test
    public void positiveCreateUser()throws DataAccessException{
        userDAO.createUser(userInfo);
    }
    @Test
    public void negativeCreateUser(){
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userDAO.createUser(userInfo);
            userDAO.createUser(userInfo);
        });
    }
    @Test
    public void positiveReadUser() throws DataAccessException{
        userDAO.createUser(userInfo);
        UserInfo good = userDAO.readUser(userInfo.username());
    }
    @Test
    public void negativeReadUser() throws DataAccessException{
        UserInfo userData = userDAO.readUser("negative");
        assertNull(userData);
    }
    @Test
    public void positiveReadGame() throws DataAccessException{
        gameDAO.createGame(game1);
        GameInfo good = gameDAO.readGame(game1.gameID());
        assertEquals(game1, good);
    }
    @Test
    public void negativeReadGame() throws DataAccessException{
        GameInfo game = gameDAO.readGame(8);
        assertNull(game);
    }
    @Test
    public void positiveReadAllGames() throws DataAccessException{
        gameDAO.createGame(game1);
        gameDAO.createGame(game2);
        gameDAO.createGame(game3);

        ArrayList<GameInfo> actualGames = gameDAO.readAllGames();

        ArrayList<GameInfo> expectedGames = new ArrayList<>();
        for(GameInfo game : actualGames) {
            if(game.equals(game1) || game.equals(game2) || game.equals(game3)) {
                expectedGames.add(game);
            }
        }

        assertEquals(expectedGames.size(), 3);
        assertTrue(actualGames.containsAll(expectedGames) && expectedGames.containsAll(actualGames));

    }


    @Test
    public void positiveDeleteAllUsers()throws DataAccessException{
            userDAO.createUser(userInfo);
            userDAO.deleteAllUsers();

    }
    @Test
    public void negativeDeleteAllUsers() throws DataAccessException{
        userDAO.deleteAllUsers();
        UserInfo user = userDAO.readUser(userInfo.username());
        assertNull(user);

    }


    @Test
    public void negativeDeleteAllGames() throws DataAccessException{
        gameDAO.deleteAllGames();
        ArrayList<GameInfo> game = gameDAO.readAllGames();
        assertEquals(new ArrayList<>(), game);
    }
    @Test
    public void positiveCreateAuth() throws DataAccessException{
        authDAO.createAuth(userInfo.username());
    }
    @Test
    public void negativeCreateAuth()throws DataAccessException{
        authDAO.createAuth(userInfo.username().toLowerCase(Locale.ROOT));
    }
    @Test
    public void negativeReadAuth() throws DataAccessException{
        AuthInfo authData = authDAO.readAuth(":(");
        assertNull(authData);
    }
    @Test
    public void positiveReadAuth() throws DataAccessException{
        String token = authDAO.createAuth(userInfo.username());
        AuthInfo passAuth = authDAO.readAuth(token);
        assertEquals(userInfo.username(), passAuth.username());
    }
    @Test
    public void positiveDeleteAuth() throws DataAccessException{
        String token = authDAO.createAuth(userInfo.username());
        authDAO.deleteAuthToken(token);
    }
    @Test
    public void negativeDeleteAuth() throws DataAccessException{
        authDAO.deleteAuthData();
        AuthInfo passAuth = authDAO.readAuth(":(");
        assertNull(passAuth);
    }

    @Test
    public void positiveDeleteAllAuth() throws DataAccessException{
        authDAO.createAuth(userInfo.username());
        authDAO.deleteAuthData();
    }
    @Test
    public void negativeDeleteAllAuth() throws DataAccessException{
        authDAO.deleteAuthData();
        AuthInfo passAuth = authDAO.readAuth(":(");
        assertNull(passAuth);
    }

    @Test
    public void positiveCreateGame() throws DataAccessException{
        gameDAO.createGame(game1);
    }
    @Test
    public void badCreateGame() {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            gameDAO.createGame(game1);
            gameDAO.createGame(game1);
        });
    }

    @Test
    public void positiveDeleteGame() throws DataAccessException{
        gameDAO.createGame(game1);
        gameDAO.deleteGame(game1.gameID());
    }
    @Test
    public void badDeleteAllGames() throws DataAccessException{
        gameDAO.deleteAllGames();
        ArrayList<GameInfo> games = gameDAO.readAllGames();
        assertEquals(new ArrayList<>(), games);
    }
    @Test
    public void createAndReadMultipleUsers() throws DataAccessException {
        UserInfo user1 = new UserInfo("user1", "pass1", "email1@example.com");
        UserInfo user2 = new UserInfo("user2", "pass2", "email2@example.com");
        userDAO.createUser(user1);
        userDAO.createUser(user2);
        UserInfo retrieveUser1 = userDAO.readUser(user1.username());
        UserInfo retrieveUser2 = userDAO.readUser(user2.username());
        assertEquals(user1.username(), retrieveUser1.username(), "User1 was not correctly stored or retrieved.");
        assertEquals(user2.username(), retrieveUser2.username(), "User2 was not correctly stored or retrieved.");
    }
    @Test
    public void deleteSpecificGameAndVerify() throws DataAccessException {
        gameDAO.createGame(game1);
        gameDAO.deleteGame(game1.gameID());

        GameInfo deletedGame = gameDAO.readGame(game1.gameID());
        assertNull(deletedGame, "Game should be deleted");
    }
    @Test
    public void readNonExistentAuth() throws DataAccessException {
        AuthInfo authData = authDAO.readAuth("DNE");
        assertNull(authData, "Should return null");
    }






}
