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
        String cmd = "INSERT INTO auth (username,authToken) VALUES (?, ?)";

        try (Connection connect = DatabaseManager.getConnection();
             PreparedStatement set = connect.prepareStatement(cmd)) {

            set.setString(1, username);
            set.setString(2, uuid.toString());
            set.executeUpdate();

        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
        return uuid.toString();
    }
    //


    public AuthInfo readAuth(String authToken) throws DataAccessException {
        String cmd = "SELECT username, authToken FROM auth WHERE authToken = ?";

        try (Connection connect = DatabaseManager.getConnection();
             PreparedStatement set = connect.prepareStatement(cmd)) {

            set.setString(1, authToken);
            try (ResultSet rs = set.executeQuery()) {
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
            try (var set = connect.prepareStatement(cmd)) {
                set.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }






}
