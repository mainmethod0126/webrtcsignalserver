package io.github.mainmethod0126.webrtcsignalserver.configs;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.mainmethod0126.webrtcsignalserver.controllers.SignalMessageController;
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

            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

                SignalMessage signalMessage = objectMapper.readValue(message.getPayload(), SignalMessage.class);
                signalMessageController.resolve(session.getId(), signalMessage);
            }

            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {

                // URL에서 roomId 파라미터 추출
                String roomId = session.getUri().getQuery().split("=")[1];
                roomService.join(roomId, session);

            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
            }

        };

        return textWebSocketHandler;
    }

}
