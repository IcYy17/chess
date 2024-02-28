package model;
import chess.ChessGame;


public record GameInfo(Integer gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    }
