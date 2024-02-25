package model;

import chess.ChessGame;

public class Model {
    private String username;
    private String email;
    private String password;

    public Model(String username,String email,String password){
        this.email =  email;
        this.password = password;
        this.username = username;
    }
    public String getEmail(){
        return email;
    }
    public String getUsername(){
        return username;
    }
    public String getPassword(){
        return password;
    }

}

