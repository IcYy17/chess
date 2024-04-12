package dataAccess;

import java.sql.*;
import com.google.gson.Gson;
import chess.*;
import model.*;

//changes here is it doesn't work after adjustments
public class WebSocketAccess implements WebSocketDAO {
    public GameInfo makeMove(int gameID, String authToken, ChessMove move) throws DataAccessException {
        try (Connection connect = DatabaseManager.getConnection()) {

            String newSql = "SELECT * FROM game WHERE gameID = ?";
            PreparedStatement state = connect.prepareStatement(newSql);
            state.setInt(1, gameID);
            ResultSet set = state.executeQuery();

            if (set.next()) {
                String whiteUsername = set.getString("whiteUsername");
                String blackUsername = set.getString("blackUsername");

                String username = getAuth(authToken).username();
                if (!username.equals(whiteUsername) && !username.equals(blackUsername)) {
                    throw new DataAccessException("Unauthorized, cannot make move");
                }
                ChessGame game = convertJsonToChessGame(set.getString("game"));
                try {
                    if (game.getTeamTurn() == ChessGame.TeamColor.FINISHED) {
                        throw new DataAccessException("Bad Request, game is over");
                    }
                    String piece = game.getBoard().getPiece(move.getStartPosition()).getTeamColor().toString();

                    if (piece.equals("WHITE") && !username.equals(whiteUsername)) {

                        throw new DataAccessException("Unauthorized, invalid color");
                    } else if (piece.equals("BLACK") && !username.equals(blackUsername)) {
                        throw new DataAccessException("Unauthorized, invalid color");
                    }
                    game.makeMove(move);
                } catch (InvalidMoveException e) {
                    throw new DataAccessException("Bad Request, invalid move");
                }
                String gameJ = new Gson().toJson(game);

                String sql2 = "UPDATE game SET game = ? WHERE gameID = ?";

                PreparedStatement state2 = connect.prepareStatement(sql2);
                state2.setString(1, gameJ);
                state2.setInt(2, gameID);
                state2.executeUpdate();
            }
            return new GameInfo(set.getInt("gameID"), set.getString("whiteUsername"), set.getString("blackUsername"), set.getString("gameName"), convertJsonToChessGame(set.getString("game")));
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("Server Error");
        }
    }

    public String leaveGame(int gameID, String authToken) throws DataAccessException {
        try (Connection connect = DatabaseManager.getConnection()) {
            String newSql = "DELETE FROM game WHERE gameID = ? AND blackUsername = ?";

            PreparedStatement state = connect.prepareStatement(newSql);
            var username = getAuth(authToken).username();
            state.setInt(1, gameID);
            state.setString(2, username);
            state.executeUpdate();
            String sql2 = "SELECT * FROM game WHERE gameID = ?";

            PreparedStatement state2 = connect.prepareStatement(sql2);
            state2.setInt(1, gameID);
            ResultSet set = state2.executeQuery();

            if (set.next()) {
                String whiteUsername = set.getString("whiteUsername");
                String blackUsername = set.getString("blackUsername");
                if (whiteUsername != null && whiteUsername.equals(username)) {
                    whiteUsername = null;
                } else if (blackUsername != null && blackUsername.equals(username)) {
                    blackUsername = null;
                }
                String sql3 = "UPDATE game SET whiteUsername = ?, blackUsername = ? WHERE gameID = ?";

                PreparedStatement state3 = connect.prepareStatement(sql3);
                state3.setString(1, whiteUsername);
                state3.setString(2, blackUsername);
                state3.setInt(3, gameID);
                state3.executeUpdate();
            }
            return username;
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("Error: Server Error");
        }
    }

    //add resign, get data, user from auth and helpers
    public String resignGame(int gameID, String authToken) throws DataAccessException {
        try (Connection connect = DatabaseManager.getConnection()) {

            String newSql = "SELECT * FROM game WHERE gameID = ?";
            PreparedStatement state = connect.prepareStatement(newSql);

            state.setInt(1, gameID);
            ResultSet set = state.executeQuery();
            if (set.next()) {
                ChessGame game = convertJsonToChessGame(set.getString("game"));
                if (game.getTeamTurn() == ChessGame.TeamColor.FINISHED) {
                    throw new DataAccessException("Bad Request, game is now over");
                }
                game.setTeamTurn(ChessGame.TeamColor.FINISHED);
                String whiteUsername = set.getString("whiteUsername");
                String blackUsername = set.getString("blackUsername");

                if (whiteUsername.equals(getAuth(authToken).username())) {
                    whiteUsername = null;
                } else if (blackUsername.equals(getAuth(authToken).username())) {
                    blackUsername = null;
                } else {
                    throw new DataAccessException("Unauthorized, no player in the game");
                }
                String sql2 = "UPDATE game SET whiteUsername = ?, blackUsername = ?, game = ? WHERE gameID = ?";
                PreparedStatement state2 = connect.prepareStatement(sql2);

                state2.setString(1, whiteUsername);
                state2.setString(2, blackUsername);
                state2.setString(3, new Gson().toJson(game));
                state2.setInt(4, gameID);

                state2.executeUpdate();
            }
            return getAuth(authToken).username();
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("Error: Server Error");
        }
    }

    public GameInfo getGameData(int gameID, String authToken) {
        try (Connection connect = DatabaseManager.getConnection()) {

            String newSql = "SELECT * FROM game WHERE gameID = ?";
            PreparedStatement state = connect.prepareStatement(newSql);

            state.setInt(1, gameID);
            ResultSet set = state.executeQuery();
            if (set.next()) { return createGameDataFromResultSet(set); }

            return null;
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUsernameFromAuthToken(String authToken) {
        try {
//            String auth = new token;
            return getAuth(authToken).username();

        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private AuthInfo getAuth(String authToken) throws SQLException, DataAccessException {
        try (Connection connect = DatabaseManager.getConnection()) {
            String newSql = "SELECT * FROM auth WHERE authToken = ?";

            PreparedStatement state = connect.prepareStatement(newSql);
            state.setString(1, authToken);
            ResultSet set = state.executeQuery();

            if (set.next()) {
                return new AuthInfo(set.getString("username"), set.getString("authToken"));
            } else {
                return null;
            }
        }
    }

    private ChessGame convertJsonToChessGame(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, ChessGame.class);
    }

    private GameInfo createGameDataFromResultSet(ResultSet resultSet) throws SQLException {
        return new GameInfo( resultSet.getInt("gameID"), resultSet.getString("whiteUsername"), resultSet.getString("blackUsername"), resultSet.getString("gameName"),  convertJsonToChessGame(resultSet.getString("game")));
    }
}
