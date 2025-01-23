package gijin.chatbot.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Data
@Getter
@Setter
public class UserDto
{
    private String userid;
    private String username;
    private String password;
    private String email;
}
