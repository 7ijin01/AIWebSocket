package gijin.chatbot.service;

import gijin.chatbot.dto.ChatRequest;
import gijin.chatbot.dto.ChatResponse;
import gijin.chatbot.entity.User;
import gijin.chatbot.openAI.OpenAIService;

import gijin.chatbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ChatService {

    // STOMP 메시지를 클라이언트로 보내기 위한 템플릿 객체
    private final SimpMessagingTemplate messagingTemplate;
    private final OpenAIService openAIService;
    @Autowired
    private final UserRepository userRepository;



    // 클라이언트로부터 받은 메시지를 처
    public void processMessage(ChatRequest.ChatMessageDTO requestDTO) {
        // 클라이언트로부터 입력받은 메세지 저장

        List<User> userList=userRepository.findAll();
        String userInfo=userList.stream()
                .map(User::getUsername)
                .collect(Collectors.joining(", "));
        // 응답받은 메세지를 DTO에 담
        String userMessage = requestDTO.getContent()+ "DB: " +userInfo;
        // userMessage 를 openAI에 전달 후 받은 응답을 aiResponse 에 저장
        String aiResponse = openAIService.askOpenAI(userMessage);

        ChatResponse.ChatMessageDTO aiMessage = new ChatResponse.ChatMessageDTO(aiResponse);
        //  /topic/messages 경로로 전달해 클라이언트가 메세지를 전달받음
        messagingTemplate.convertAndSend("/topic/messages", aiMessage);
    }
}
