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

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.mainmethod0126.webrtcsignalserver.dtos.Attendee;
import io.github.mainmethod0126.webrtcsignalserver.dtos.Room;
import io.github.mainmethod0126.webrtcsignalserver.dtos.SignalMessage;
import lombok.var;

@Service
public class RoomService {

    private Map<String, Room> roomStorage = new HashMap<>();

    public List<Room> getRoomList() {
        return roomStorage.values().stream().collect(Collectors.toList());
    }

    public void join(String roomId, WebSocketSession session, String userId) {

        var attendee = Attendee.builder().id(userId).session(session).build();

        roomStorage.compute(roomId, (key, value) -> {
            if (value == null) {
                List<Attendee> attendees = new ArrayList<>();
                attendees.add(attendee);
                return Room.builder().id(UUID.randomUUID().toString()).attendees(attendees).build();
            } else {
                // 이미 참석한 userId가 아닐 경우에만 참석자에 추가
                if (!value.isContainByAttendeeId(userId)) {
                    value.getAttendees().add(attendee);
                }

                return value;
            }
        });

    }

    public Attendee getAttendee(String roomId, String sessionId) {
        return roomStorage.get(roomId).getAttendees().stream().filter((attendee) -> {
            return attendee.getSession().getId().equals(sessionId);
        }).findFirst().get();
    }

    // 스레드 깨우는 notify 함수가 있어서 작명에 SignalMessage를 추가함
    public void notifySignalMessage(String roomId, Attendee messageSender, SignalMessage signalMessage) {
        Objects.requireNonNull(signalMessage, "signalMessage is null");
        signalMessage.setFrom(messageSender.getId());
        var room = roomStorage.get(roomId);

        // to 가 명시되지 않았으면 room 전체에게 전달
        if (signalMessage.getTo() == null) {
            room.getAttendees().stream().forEach((attendee) -> {

                // 상대방이 자기 자신일 경우 메세지를 전달할 필요가 없음
                if (attendee.getId().equals(messageSender.getId())) {
                    return;
                }

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.setSerializationInclusion(Include.NON_NULL);
                String convertedSignalMessage = "";
                try {
                    convertedSignalMessage = objectMapper.writeValueAsString(signalMessage);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

                try {
                    attendee.getSession().sendMessage(new TextMessage(convertedSignalMessage));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else { // to 가 명시되어있으면 일치하는 세션ID 한명에게 전달
            room.getAttendees().stream().filter((attendee) -> {
                return attendee.getId().equals(signalMessage.getTo());
            }).findFirst().ifPresent((attendee) -> {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.setSerializationInclusion(Include.NON_NULL);
                String convertedSignalMessage = "";
                try {
                    convertedSignalMessage = objectMapper.writeValueAsString(signalMessage);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

                try {
                    attendee.getSession().sendMessage(new TextMessage(convertedSignalMessage));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

    }

    public void leaveBySessionId(String sessionId) {
        roomStorage.values().stream()
                .filter(r -> r.isContainBySessionId(
                        sessionId))
                .findFirst().ifPresent((room) -> {
                    room.leaveBySessionId(sessionId);
                });

    }

    public void leaveByAttendeeId(String attendeeId) {
        roomStorage.values().stream()
                .filter(r -> r.isContainByAttendeeId(
                        attendeeId))
                .findFirst().ifPresent((room) -> {
                    room.leaveByAttendeeId(attendeeId);
                });

    }

}
