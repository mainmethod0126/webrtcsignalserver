package io.github.mainmethod0126.webrtcsignalserver.controllers;

import org.springframework.stereotype.Controller;

import io.github.mainmethod0126.webrtcsignalserver.dtos.SignalMessage;
import io.github.mainmethod0126.webrtcsignalserver.services.RoomService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class SignalMessageController {

    private final RoomService roomService;

    public void resolve(String sessionId, SignalMessage signalMessage) {
        roomService.notifySignalMessage(sessionId, signalMessage);
    }

}
