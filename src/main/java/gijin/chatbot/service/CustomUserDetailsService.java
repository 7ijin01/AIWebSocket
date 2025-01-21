package gijin.chatbot.service;

import gijin.chatbot.dto.CustomUserDetails;
import gijin.chatbot.entity.User;
import gijin.chatbot.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public class CustomUserDetailsService implements UserDetailsService
{
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Optional<User> user =userRepository.findByUserId(userId);
        if(user.get()==null)
        {
            return null;
        }
        return new CustomUserDetails(user.get());
    }
}
