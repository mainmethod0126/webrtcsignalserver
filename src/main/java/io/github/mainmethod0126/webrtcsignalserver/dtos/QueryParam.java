package io.github.mainmethod0126.webrtcsignalserver.dtos;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class QueryParam {

    private String roomId;
    private String userId;
}
