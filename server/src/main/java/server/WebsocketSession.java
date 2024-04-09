package server;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jetty.websocket.api.Session;
public class WebsocketSession {
    private Map<Integer, Map<String, Session>> sessionMap;

    public WebsocketSession() {
        sessionMap = new HashMap<>();
    }

    public void addSessionToGame(Integer gameID, String authToken, Session session) {

    }

    public void removeSessionFromGame(Integer gameID, String authToken, Session session) {

    }
    public void removeSession(Session session) {

    }
    public Map<String, Session> getSessionsForGame(Integer gameID) {
        return null;
    }
}
