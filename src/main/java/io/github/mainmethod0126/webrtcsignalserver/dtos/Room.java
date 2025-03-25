package io.github.mainmethod0126.webrtcsignalserver.dtos;

import java.util.List;

import org.springframework.web.socket.WebSocketSession;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Room {

    private String id;
    private List<WebSocketSession> attendees;

    public boolean isContain(String sessionId) {
        return attendees.stream().anyMatch((a) -> {
            return a.getId().equals(sessionId);
        });
    }

    public void leave(String sessionId) {
        attendees.removeIf((attendee) -> {
            return attendee.getId().equals(sessionId);
        });
    }
}
