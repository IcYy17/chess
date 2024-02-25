package model;

import chess.ChessGame;

public class model {
    public record User(String username, String password, String email) { }
    public record Game(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) { }
    public record AuthToken(String authToken, String username) { }

}
