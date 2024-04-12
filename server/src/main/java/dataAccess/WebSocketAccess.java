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


}
