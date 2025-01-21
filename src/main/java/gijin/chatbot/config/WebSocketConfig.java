package gijin.chatbot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
// STOMP 프로토콜을 사용하여 WebSocket을 통해 메시지를 주고받을 수 있음.
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // 메세지 라우터
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // /topic 이 붙은 목적지를 구독하는 클라이언트에게 메세지 전달
        config.enableSimpleBroker("/topic");
        // 클라이언트가 메세지를 보낼때 목적지의 접두사
        config.setApplicationDestinationPrefixes("/app");
    }

    // stomp 엔드포인트 등록. 엔드포인트를 통해 WebSocket 연결 시작
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // /chat-websocket 를 엔드포인트로 설정. 웹소켓 연결 시작
        registry.addEndpoint("/chat-websocket").withSockJS();
    }
}
