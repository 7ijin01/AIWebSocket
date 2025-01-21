package gijin.chatbot.dto;

import lombok.Data;

public class ChatResponse
{
    @Data
    public static class ChatMessageDTO
    {
        private String content;
        private String sender;

        public ChatMessageDTO(String content) {
            this.content = content;
            this.sender="AI";
        }
    }
}
