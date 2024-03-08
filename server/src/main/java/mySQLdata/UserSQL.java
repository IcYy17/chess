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
             PreparedStatement statement = connect.prepareStatement(cmd)) {

            statement.setString(1, user.username());
            statement.setString(2, hashedPassword);
            statement.setString(3, user.email());
            statement.executeUpdate();

        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    public UserInfo readUser(String username) throws DataAccessException {
        try (Connection connect = DatabaseManager.getConnection()) {
            String cmd = "SELECT * FROM user WHERE username = ?";
            try (var state = connect.prepareStatement(cmd)) {
                state.setString(1,username);
                ResultSet rs = state.executeQuery();
                if(rs.next()){
                    return new UserInfo(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email")
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








