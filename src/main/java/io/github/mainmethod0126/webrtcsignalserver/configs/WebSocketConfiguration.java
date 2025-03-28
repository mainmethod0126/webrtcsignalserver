package io.github.mainmethod0126.webrtcsignalserver.configs;

import java.net.URI;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.mainmethod0126.webrtcsignalserver.controllers.SignalMessageController;
import io.github.mainmethod0126.webrtcsignalserver.dtos.QueryParam;
import io.github.mainmethod0126.webrtcsignalserver.dtos.SignalMessage;
import io.github.mainmethod0126.webrtcsignalserver.services.RoomService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    private ObjectMapper objectMapper = new ObjectMapper();
    private final SignalMessageController signalMessageController;
    private final RoomService roomService;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(signalHandler(), "/signal").setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler signalHandler() {

        var textWebSocketHandler = new TextWebSocketHandler() {

            private QueryParam getQueryParams(URI uri) {
                if (uri != null) {
                    UriComponents uriComponents = UriComponentsBuilder.fromUri(uri).build();
                    Map<String, String> queryParams = uriComponents.getQueryParams()
                            .toSingleValueMap();
                    String roomId = queryParams.getOrDefault("roomId", "defaultRoom");
                    String userId = queryParams.getOrDefault("userId", "anonymousUser");
                    return QueryParam.builder().roomId(roomId).userId(userId).build();
                } else {
                    return QueryParam.builder().roomId("defaultRoom").userId("anonymousUser").build();
                }

            }

            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

                QueryParam queryParam = getQueryParams(session.getUri());
                SignalMessage signalMessage = objectMapper.readValue(message.getPayload(), SignalMessage.class);
                System.out.println("received signalMessage from userID : " + queryParam.getUserId());
                signalMessageController.resolve(session.getId(), queryParam.getRoomId(), signalMessage);
            }

            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {

                QueryParam queryParam = getQueryParams(session.getUri());
                roomService.join(queryParam.getRoomId(), session, queryParam.getUserId());
                System.out.println("joined : " + queryParam.getUserId());
                // URI가 null인 경우 기본값 사용
                roomService.join("defaultRoom", session, "anonymousUser");
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
                roomService.leaveBySessionId(session.getId());
            }

        };

        return textWebSocketHandler;
    }

}
