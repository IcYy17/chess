package ui;

import chess.ChessGame;

public interface GameBoardHandler {
    void updateGame(ChessGame game, String whiteUsername, String blackUsername);
    void printMessage(String message);
}
