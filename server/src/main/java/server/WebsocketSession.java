package server;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.jetty.websocket.api.Session;
public class WebsocketSession {
    private Map<Integer, Map<String, Session>> sessions;

    public WebsocketSession() {
        sessions = new HashMap<>();
    }

    public void addSessionToGame(Integer gameID, String authToken, Session session) {
        sessions.computeIfAbsent(gameID, k -> new HashMap<>()).put(authToken, session);
    }

    public void removeSessionFromGame(Integer gameID, String authToken, Session session) {
        sessions.getOrDefault(gameID, Collections.emptyMap()).remove(authToken);
    }
    public void removeSession(Session session) {
//        for (Map<String, Session> gameSessions : sessions.values()) {
//
//            Set<String> keysToRemove = gameSessions.entrySet().stream()
//                    .filter(entry -> entry.getValue().equals(session))
//                    .map(Map.Entry::getKey)
//                    .collect(Collectors.toSet());
//
//
//            for (String key : keysToRemove) {
//                gameSessions.remove(key);
//            }
//        }
        for (Map<String, Session> map : sessions.values()) {
            map.values().remove(session);
        }

    }
    public Map<String, Session> getSessionsForGame(Integer gameID) {
        return sessions.get(gameID);
    }
}
