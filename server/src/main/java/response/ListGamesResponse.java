package response;

import model.*;

import java.util.ArrayList;

public record ListGamesResponse(ArrayList<GameInfo> games) {
}
