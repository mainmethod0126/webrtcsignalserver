package io.github.mainmethod0126.webrtcsignalserver.dtos;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Room {

    private String id;
    private List<Attendee> attendees;

    public boolean isContainBySessionId(String sessionId) {
        return attendees.stream().anyMatch((attendee) -> {
            return attendee.getSession().getId().equals(sessionId);
        });
    }

    public boolean isContainByAttendeeId(String attendeeId) {
        return attendees.stream().anyMatch((attendee) -> {
            return attendee.getId().equals(attendeeId);
        });
    }

    public void leaveBySessionId(String sessionId) {
        if (attendees.removeIf((attendee) -> {
            return attendee.getSession().getId().equals(sessionId);
        })) {
            System.out.println("leave : " + sessionId);
        }
    }

    public void leaveByAttendeeId(String attendeeId) {
        if (attendees.removeIf((attendee) -> {
            return attendee.getId().equals(attendeeId);
        })) {
            System.out.println("leave : " + attendeeId);
        }
    }
}
