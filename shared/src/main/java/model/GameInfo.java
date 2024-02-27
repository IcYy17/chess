package model;
import chess.ChessGame;


public record GameInfo(String gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    }
