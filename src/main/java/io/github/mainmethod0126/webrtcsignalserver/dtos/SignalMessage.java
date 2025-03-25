package io.github.mainmethod0126.webrtcsignalserver.dtos;

import io.github.mainmethod0126.webrtcsignalserver.enums.WebRTCSignalMessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignalMessage {

    private WebRTCSignalMessageType type;
    private String roomId;
    private String sdp;

}
