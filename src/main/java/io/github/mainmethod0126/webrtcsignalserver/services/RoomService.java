package io.github.mainmethod0126.webrtcsignalserver.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.mainmethod0126.webrtcsignalserver.dtos.Room;
import io.github.mainmethod0126.webrtcsignalserver.dtos.SignalMessage;

@Service
public class RoomService {

    private Map<String, Room> roomStorage = new HashMap<>();

    public List<Room> getRoomList() {
        return roomStorage.values().stream().collect(Collectors.toList());
    }

    public void join(String roomId, WebSocketSession attendee) {

        roomStorage.compute(roomId, (key, value) -> {
            if (value == null) {
                List<WebSocketSession> attendees = new ArrayList<>();
                attendees.add(attendee);
                return Room.builder().id(UUID.randomUUID().toString()).attendees(attendees).build();
            } else {
                value.getAttendees().add(attendee);
                return value;
            }
        });

    }

    // 스레드 깨우는 notify 함수가 있어서 작명에 SignalMessage를 추가함
    public void notifySignalMessage(String sessionId, SignalMessage signalMessage) {

        Objects.requireNonNull(signalMessage, "signalMessage is null");
        String roomId = Objects.requireNonNull(signalMessage.getRoomId(), "signalMessage.getRoomId() is null");

        var room = roomStorage.get(roomId);

        room.getAttendees().stream().forEach((attendee) -> {

            // 상대방이 자기 자신일 경우 메세지를 전달할 필요가 없음
            if (attendee.getId().equals(sessionId)) {
                return;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            String convertedSignalMessage = "";
            try {
                convertedSignalMessage = objectMapper.writeValueAsString(signalMessage);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            try {
                attendee.sendMessage(new TextMessage(convertedSignalMessage));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    public void leave(String sessionId) {
        roomStorage.values().stream()
                .filter(r -> r.isContain(sessionId))
                .findFirst().ifPresent((room) -> {
                    room.leave(sessionId);
                });
    }

}
