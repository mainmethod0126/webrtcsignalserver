package io.github.mainmethod0126.webrtcsignalserver;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class SessionStorage {

    private ConcurrentHashMap<String, WebSocketSession> sessions;

    public void putIfAbsent(String sessionId, WebSocketSession session) {
        sessions.putIfAbsent(sessionId, session);
    }

}
