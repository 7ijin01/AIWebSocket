package gijin.chatbot.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class ChatRequest {

    @Getter
    @Setter
    public static class ChatMessageDTO {
        // 요청 내용
        private String content;
        // 클라이언트 이름
        private String sender;

    }

}