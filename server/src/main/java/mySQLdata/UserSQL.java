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
        String sql = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, user.username());
            statement.setString(2, hashedPassword);
            statement.setString(3, user.email());
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
    public void deleteUsers() throws DataAccessException{
        try (Connection conn = DatabaseManager.getConnection()) {
            String cmd = "DELETE FROM user";
            try (var send = conn.prepareStatement(cmd)) {
                send.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public UserInfo readUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT * FROM user WHERE username = ?";
            try (var statement = conn.prepareStatement(sql)) {
                statement.setString(1,username);
                ResultSet rs = statement.executeQuery();
                if(rs.next()){
                    return new UserInfo(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email")
                    );
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }






}








