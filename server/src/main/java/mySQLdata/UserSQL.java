package mySQLdata;
import dataAccess.DataAccessException;
import model.UserInfo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import dataAccess.DatabaseManager;
public class UserSQL {
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
            throw new DataAccessException("Failed to create user: " + e.getMessage(), e);
        }
    }
}







}
