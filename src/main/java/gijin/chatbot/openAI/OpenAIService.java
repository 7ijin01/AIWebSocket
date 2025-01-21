package gijin.chatbot.openAI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.util.*;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.util.*;
@Service
public class OpenAIService {

    // application.yml 에서 api key 주입받음
    @Value("${openai.api.key}")
    private String apiKey;

    // 요청 api 주소
    private final String apiUrl = "https://api.openai.com/v1/chat/completions";
    // RESTful 웹 서비스 사용위해
    private final RestTemplate restTemplate = new RestTemplate();

    // 질문 받고 응답

    //requestBody: API 요청의 본문을 담을 Map 객체입니다.
    //모델 이름 지정: "model" 키를 사용하여 "gpt-4o-mini" 모델을 사용한다고 명시합니다.
    //메시지 내용 지정: "messages" 키를 사용하여, 사용자가 보낸 메시지를 담고 있습니다. 이 메시지는 "role"이 "user"로, "content"에 prompt 내용이 들어갑니다
    public String askOpenAI(String prompt) {

        // 요청할 모델 지정
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o-mini");
        requestBody.put("messages", List.of(
                Map.of("role", "user", "content", prompt)
        ));

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        // 헤더 바디를 포함한 요청 객체 생성
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        // api 요청 후 응답 받음
        ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);

        // 응답된 메세지 반환
        if (response.getBody() != null) {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                return message != null ? (String) message.get("content") : "No response content";
            }
        }
        return "Error: No response from OpenAI";
    }
}

