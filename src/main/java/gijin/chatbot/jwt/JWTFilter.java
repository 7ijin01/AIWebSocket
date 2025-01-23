package gijin.chatbot.jwt;

import gijin.chatbot.dto.CustomUserDetails;
import gijin.chatbot.entity.User;
import gijin.chatbot.repository.AccessRepository;
import gijin.chatbot.util.NetworkUtils;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

public class JWTFilter extends OncePerRequestFilter
{
    private final JWTUtil jwtUtil;
    private final AccessRepository accessRepository;

    public JWTFilter(JWTUtil jwtUtil, AccessRepository accessRepository) {
        this.jwtUtil = jwtUtil;
        this.accessRepository = accessRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 헤더에서 access키에 담긴 토큰을 꺼냄
        String accessToken = request.getHeader("Authorization");

// 토큰이 없다면 다음 필터로 넘김
        if (accessToken == null) {

            filterChain.doFilter(request, response);

            return;
        }

// 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("access token expired");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

// 토큰이 access인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("Authorization")) {

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }


        String userId = jwtUtil.getUserId(accessToken);
        String username = jwtUtil.getUserName(accessToken);
        String role = jwtUtil.getRole(accessToken);
        System.out.println(username +" "+role+" "+userId+"Asdlkajsdlashjfiondfkljdklajmd");

        //요청보내는 장치의 ip 주소가 다르면 로그인 경로로 보냉ㅇ

        String ip=accessRepository.findByUserid(userId).get().getIp();
        if (!NetworkUtils.getLocalIpAddress().equals(ip)) {
            // ip 불일치 시 응답 상태를 UNAUTHORIZED로 설정하고, 리다이렉트를 수행합니다.
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            // 로그인 페이지로 리다이렉트
            response.sendRedirect("/login");

            return;
        }





        User userEntity = new User();
        userEntity.setUserid(userId);
        userEntity.setUsername(username);
        userEntity.setRole(role);
        CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);



    }
}




