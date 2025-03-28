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

    // offer 의 경우 from 이 필수로 있어야한다
    private String from;

    // answer 의 경우 to 가 필수로 있어야한다
    private String to;

}
