package io.github.mainmethod0126.webrtcsignalserver.controllers;

import org.springframework.stereotype.Controller;

import io.github.mainmethod0126.webrtcsignalserver.dtos.SignalMessage;
import io.github.mainmethod0126.webrtcsignalserver.services.RoomService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class SignalMessageController {

    private final RoomService roomService;

    public void resolve(String sessionId, String roomId, SignalMessage signalMessage) {

        roomService.notifySignalMessage(roomId, roomService.getAttendee(roomId, sessionId), signalMessage);
    }

}
