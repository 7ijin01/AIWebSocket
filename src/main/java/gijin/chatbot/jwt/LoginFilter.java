package gijin.chatbot.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import gijin.chatbot.dto.CustomUserDetails;
import gijin.chatbot.entity.AccessEntity;
import gijin.chatbot.entity.RefreshEntity;
import gijin.chatbot.entity.User;
import gijin.chatbot.repository.AccessRepository;
import gijin.chatbot.repository.RefreshRepository;

import gijin.chatbot.repository.UserRepository;
import gijin.chatbot.util.NetworkUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

public class LoginFilter extends UsernamePasswordAuthenticationFilter
{
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final AccessRepository accessRepository;


    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, RefreshRepository refreshRepository, AccessRepository accessRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
        this.accessRepository = accessRepository;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {

            ObjectMapper objectMapper=new ObjectMapper();
            Map<String, String> credentials=objectMapper.readValue(request.getInputStream(),Map.class);

            String userId=credentials.get("userid");

            String password= credentials.get("password");

            UsernamePasswordAuthenticationToken token=new UsernamePasswordAuthenticationToken(userId,password,null);

            return authenticationManager.authenticate(token);

        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {

        //유저 정보
        CustomUserDetails customUserDetails= (CustomUserDetails)authentication.getPrincipal();
        String username = customUserDetails.getUsername();
        String userId = customUserDetails.getUserID();



        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();


        //토큰 생성
        String access = jwtUtil.createJwt("Authorization", userId,username, role, 600000L);
        String refresh = jwtUtil.createJwt("Refresh",userId ,username, role, 86400000L);
        
        //로그인상태에서 다시 로그인 했을 때 토큰만 갱신
        updateOrInsertAccessEntity(userId, username, NetworkUtils.getLocalIpAddress(), access, 600000L);
        updateOrInsertRefreshEntity(userId, username, refresh, 86400000L);





        //응답 설정
        response.setHeader("Authorization", access);
        response.addCookie(createCookie("Refresh", refresh));
        response.setStatus(HttpStatus.OK.value());
    }
    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        //cookie.setSecure(true);
        //cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
    private void updateOrInsertAccessEntity(String userId, String username, String ip, String access, Long expiredMs) {
        AccessEntity accessEntity = accessRepository.findByUserid(userId).orElse(new AccessEntity());
        accessEntity.setUserid(userId);
        accessEntity.setUsername(username);
        accessEntity.setIp(ip);
        accessEntity.setAccess(access);
        accessEntity.setExpiration(new Date(System.currentTimeMillis() + expiredMs).toString());
        accessRepository.save(accessEntity);
    }

    private void updateOrInsertRefreshEntity(String userId, String username, String refresh, Long expiredMs) {
        RefreshEntity refreshEntity = refreshRepository.findByUserid(userId).orElse(new RefreshEntity());
        refreshEntity.setUserid(userId);
        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(new Date(System.currentTimeMillis() + expiredMs).toString());
        refreshRepository.save(refreshEntity);
    }


    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
    }
    //로그인 경로 변경시 사용
//    @Override
//    public void setFilterProcessesUrl(String filterProcessesUrl) {
//        super.setFilterProcessesUrl(filterProcessesUrl);
//        setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(filterProcessesUrl));
//    }
}
