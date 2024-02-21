package model;

import chess.ChessGame;

public class model {


    public class User {
        private String username;
        private String password;
        private String email;

    }

    public class Game {
        private int gameID;
        private String whiteUsername;
        private String blackUsername;
        private String gameName;
        private ChessGame game;

    }

    public class AuthToken {
        private String authToken;
        private String username;

    }
}
