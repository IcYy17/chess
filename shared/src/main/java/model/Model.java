package model;

import chess.ChessGame;

public class Model {
   public static class UserInfo{

        private String username;
        private String email;
        private String password;

    public UserInfo(String username, String email, String password) {
        this.email = email;
        this.password = password;
        this.username = username;
    }
        public String getEmail () {
        return email;
    }
        public String getUsername () {
        return username;
    }
        public String getPassword () {
        return password;
    }
    }

    public static class Authentication {
        private final String username;
        private final String token;

        public Authentication(String username, String token) {
            this.username = username;
            this.token = token;
        }

        public String getUserIdentifier() {
            return username;
        }

        public String getToken() {
            return token;
        }
    }

    public static class gameInfo{

       private ChessGame game;
       private String whitePlayer="";
       private String blackPlayer="";
       private String matchId = "";
       private String matchName;
        public gameInfo(String matchName) {
            this.matchName = matchName;
        }

        public void setPlayerWhite(String name) {
            this.whitePlayer = name;
        }

        public void setPlayerBlack(String name) {
            this.blackPlayer = name;
        }

        public void setMatchId(String id) {
            this.matchId = id;
        }

        public String getMatchId() {
            return this.matchId;
        }

        public String getMatchName() {
            return this.matchName;
        }

        public String getPlayerWhite() {
            return this.whitePlayer;
        }

        public String getPlayerBlack() {
            return this.blackPlayer;
        }

    }

}

