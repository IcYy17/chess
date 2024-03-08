package dataAccessTests;
import chess.ChessGame;
import dataAccess.DataAccessException;
import mySQLdata.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;

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

}
