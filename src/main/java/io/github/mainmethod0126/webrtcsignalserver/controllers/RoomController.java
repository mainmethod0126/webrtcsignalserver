package io.github.mainmethod0126.webrtcsignalserver.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.mainmethod0126.webrtcsignalserver.dtos.Room;
import io.github.mainmethod0126.webrtcsignalserver.services.RoomService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    @GetMapping("")
    public ResponseEntity<List<Room>> getRoomList() {
        return ResponseEntity.ok().body(roomService.getRoomList());
    }
}