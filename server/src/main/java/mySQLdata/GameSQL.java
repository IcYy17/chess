package mySQLdata;
import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.*;
import model.GameInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GameSQL implements dataAccess.GameDAO {
    public void createGame(GameInfo game) throws DataAccessException {
        String cmd = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement set = conn.prepareStatement(cmd)) {


            String gameStateJson = new Gson().toJson(game.game());


            set.setInt(1, game.gameID());
            set.setString(2, game.whiteUsername());
            set.setString(3, game.blackUsername());
            set.setString(4, game.gameName());
            set.setString(5, gameStateJson);

            // Execute the statement as a batch (though it contains only one insert operation here)
            set.addBatch();
            set.executeBatch();

        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }
    public void deleteEveryGame() throws DataAccessException {
        try (Connection set = DatabaseManager.getConnection()) {
            String cmd = "DELETE FROM game";
            try (var statement = set.prepareStatement(cmd)) {
                statement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    public GameInfo readGame(Integer gameID) throws DataAccessException {
        final String cmd = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game WHERE gameID = ?";

        try (Connection set = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = set.prepareStatement(cmd)) {

            preparedStatement.setInt(1, gameID); // Directly setting integer without converting to String
            try (ResultSet res = preparedStatement.executeQuery()) {
                if (res.next()) {
                    // Directly using appropriate ResultSet getters for data types
                    int retrievedGameID = res.getInt("gameID");
                    String whiteUsername = res.getString("whiteUsername");
                    String blackUsername = res.getString("blackUsername");
                    String gameName = res.getString("gameName");
                    String gameData = res.getString("game"); // Assuming 'game' is stored as a JSON string

                    ChessGame chessGame = new Gson().fromJson(gameData, ChessGame.class);
                    return new GameInfo(retrievedGameID, whiteUsername, blackUsername, gameName, chessGame);
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
        return null; // Handling the case where no game is found more explicitly
    }


    public void deleteGame(Integer gameID) throws DataAccessException {
        try (Connection set = DatabaseManager.getConnection()) {
            String cmd = "DELETE FROM game WHERE gameID = ?";
            try (var statement = set.prepareStatement(cmd)) {
                statement.setString(1,gameID.toString());
                statement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    public ArrayList<GameInfo> readAllGames() throws DataAccessException {
        final String sqlQuery = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game";
        ArrayList<GameInfo> games = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int gameID = resultSet.getInt("gameID");
                String whiteUsername = resultSet.getString("whiteUsername");
                String blackUsername = resultSet.getString("blackUsername");
                String gameName = resultSet.getString("gameName");
                ChessGame chessGame = new Gson().fromJson(resultSet.getString("game"), ChessGame.class);

                games.add(new GameInfo(gameID, whiteUsername, blackUsername, gameName, chessGame));
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
        return games; // Always returns the list, even if it's empty
    }




}
