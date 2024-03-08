package mySQLdata;
import dataAccess.DataAccessException;
import model.UserInfo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import dataAccess.DatabaseManager;
public class UserSQL implements dataAccess.UserDAO {
    public BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    public void createUser(UserInfo user) throws DataAccessException {
        String hashedPassword = encoder.encode(user.password());
        String cmd = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";

        try (Connection connect = DatabaseManager.getConnection();
             PreparedStatement set = connect.prepareStatement(cmd)) {

            set.setString(1, user.username());
            set.setString(2, hashedPassword);
            set.setString(3, user.email());
            set.executeUpdate();

        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    public UserInfo readUser(String username) throws DataAccessException {
        try (Connection connect = DatabaseManager.getConnection()) {
            String cmd = "SELECT * FROM user WHERE username = ?";
            try (var state = connect.prepareStatement(cmd)) {
                state.setString(1,username);
                ResultSet set = state.executeQuery();
                if(set.next()){
                    return new UserInfo(
                           set.getString("username"),
                            set.getString("password"),
                            set.getString("email")
                    );
                }
                return null;
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    public void deleteAllUsers() throws DataAccessException{
        try (Connection connect = DatabaseManager.getConnection()) {
            String cmd = "DELETE FROM user";
            try (var state = connect.prepareStatement(cmd)) {
                state.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }





}








