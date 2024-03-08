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
    GameInfo game2 = new GameInfo(1,"white","black","chess2",new ChessGame());
    GameInfo game3 = new GameInfo(1,"white","black","chess3",new ChessGame());

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
    public void positiveDeleteAllUsers()throws DataAccessException{
            userDAO.createUser(userInfo);
            userDAO.deleteAllUsers();

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


}
