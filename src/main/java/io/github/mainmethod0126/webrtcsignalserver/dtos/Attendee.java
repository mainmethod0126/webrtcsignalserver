package io.github.mainmethod0126.webrtcsignalserver.dtos;

import org.springframework.web.socket.WebSocketSession;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Attendee {

    private String id;
    private WebSocketSession session;
}
