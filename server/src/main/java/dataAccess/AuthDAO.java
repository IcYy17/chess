package dataAccess;

import chess.ChessMove;
import exception.ResponseException;
import model.AuthInfo;
import model.GameInfo;

public interface AuthDAO {
    String createAuth(String username)throws DataAccessException;
    AuthInfo readAuth(String authToken)throws DataAccessException;
    void deleteAuthToken(String authToken)throws DataAccessException;
    void deleteAuthData() throws DataAccessException;

}
