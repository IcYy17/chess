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

        try (Connection connect = DatabaseManager.getConnection();
             PreparedStatement set = connect.prepareStatement(cmd)) {
            String gameStateJson = new Gson().toJson(game.game());
            set.setInt(1, game.gameID());
            set.setString(2, game.whiteUsername());
            set.setString(3, game.blackUsername());
            set.setString(4, game.gameName());
            set.setString(5, gameStateJson);
            set.addBatch();
            set.executeBatch();

        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }
    public void deleteAllGames() throws DataAccessException {
        try (Connection set = DatabaseManager.getConnection()) {
            String cmd = "DELETE FROM game";
            try (var state = set.prepareStatement(cmd)) {
                state.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    public GameInfo readGame(Integer gameID) throws DataAccessException {
        final String cmd = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game WHERE gameID = ?";

        try (Connection set = DatabaseManager.getConnection();
             PreparedStatement state = set.prepareStatement(cmd)) {

            state.setInt(1, gameID);
            try (ResultSet res = state.executeQuery()) {
                if (res.next()) {
                    // Directly using appropriate ResultSet getters for data types
                    int retrievedGameID = res.getInt("gameID");
                    String whiteUser = res.getString("whiteUsername");
                    String blackUser = res.getString("blackUsername");
                    String gameNum = res.getString("gameName");
                    String gameInfo = res.getString("game");

                    ChessGame game = new Gson().fromJson(gameInfo, ChessGame.class);
                    return new GameInfo(retrievedGameID, whiteUser, blackUser, gameNum, game);
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
        return null;
    }


    public void deleteGame(Integer gameID) throws DataAccessException {
        try (Connection set = DatabaseManager.getConnection()) {
            String cmd = "DELETE FROM game WHERE gameID = ?";
            try (var state = set.prepareStatement(cmd)) {
                state.setString(1,gameID.toString());
                state.executeUpdate();
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
                String whiteUser = resultSet.getString("whiteUsername");
                String blackUser = resultSet.getString("blackUsername");
                String gameNum = resultSet.getString("gameName");
                ChessGame chessGame = new Gson().fromJson(resultSet.getString("game"), ChessGame.class);

                games.add(new GameInfo(gameID, whiteUser, blackUser, gameNum, chessGame));
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
        return games;
    }




}
