package gijin.chatbot.controller;


import gijin.chatbot.dto.ChatRequest;
import gijin.chatbot.service.ChatService;
import lombok.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;



@RequiredArgsConstructor
@Controller
public class ChatController {

    private final ChatService chatService;

    // 클라이언트가 입력한 메세지 서버로 전달
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatRequest.ChatMessageDTO requestDTO) {
        chatService.processMessage(requestDTO);
    }
    // 클라이언트 정보 저장
    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatRequest.ChatMessageDTO chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
    }
}
