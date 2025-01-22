package gijin.chatbot.controller;

import gijin.chatbot.dto.UserDto;
import gijin.chatbot.service.UserService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JoinController
{
    private final UserService userService;

    public JoinController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/join")
    public ResponseEntity<?> JoinProcess(@RequestBody UserDto userDto)
    {
        System.out.println(userDto.getPassword());
        System.out.println("Asdasdasdasdadas");
        userService.registerUser(userDto);

        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }
}
