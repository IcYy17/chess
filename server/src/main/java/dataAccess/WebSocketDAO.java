package dataAccess;

import chess.ChessMove;
import model.GameInfo;

public interface WebSocketDAO {
    GameInfo makeMove(int gameID, String authToken, ChessMove move) throws DataAccessException;
    String leaveGame(int gameID, String authToken) throws DataAccessException;
    String resignGame(int gameID, String authToken) throws DataAccessException;
    GameInfo getGameData(int gameID, String authToken);
    String getUsernameFromAuthToken(String authToken);
}
