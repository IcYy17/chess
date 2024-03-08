package mySQLdata;

import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import model.AuthInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AuthSQL implements dataAccess.AuthDAO {
    public String createAuth(String username) throws DataAccessException {
        UUID uuid = UUID.randomUUID();
        String sql = "INSERT INTO auth (username,authToken) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, uuid.toString());
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return uuid.toString();
    }
    //


    public AuthInfo readAuth(String authToken) throws DataAccessException {
        String sql = "SELECT username, authToken FROM auth WHERE authToken = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, authToken);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return new AuthInfo(
                            rs.getString("username"),
                            authToken
                    );
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException( ex.getMessage());}
        return null;
    }
//

    public void deleteAuthToken(String authToken) throws DataAccessException {
        try (Connection connect = DatabaseManager.getConnection()) {
            String cmd = "DELETE FROM auth WHERE authToken = ?";
            try (var set = connect.prepareStatement(cmd)) {
                set.setString(1,authToken);
                set.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    public void deleteAuthData() throws DataAccessException {
        try (Connection connect = DatabaseManager.getConnection()) {
            String cmd = "DELETE FROM auth";
            try (var statement = connect.prepareStatement(cmd)) {
                statement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }




}
