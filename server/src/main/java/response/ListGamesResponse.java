package response;
import java.util.ArrayList;
import model.*;



public record ListGamesResponse(ArrayList<GameInfo> games) {
}
