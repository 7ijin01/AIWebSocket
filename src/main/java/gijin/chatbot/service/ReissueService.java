package gijin.chatbot.service;


import gijin.chatbot.entity.AccessEntity;
import gijin.chatbot.entity.RefreshEntity;
import gijin.chatbot.jwt.JWTUtil;
import gijin.chatbot.repository.AccessRepository;
import gijin.chatbot.repository.RefreshRepository;
import gijin.chatbot.util.NetworkUtils;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ReissueService
{
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final AccessRepository accessRepository;

    public ReissueService(JWTUtil jwtUtil, RefreshRepository refreshRepository, AccessRepository accessRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
        this.accessRepository = accessRepository;
    }

    public ResponseEntity<?> ReissueAccessToken(HttpServletRequest request, HttpServletResponse response)
    {
        String refresh=null;
        Cookie[] cookies= request.getCookies();
        for(Cookie cookie:cookies)
        {
            if(cookie.getName().equals("Refresh"))
            {
                refresh =cookie.getValue();
            }
        }

        if(refresh == null)
        {
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }
        try{
            jwtUtil.isExpired(refresh);
        }catch (ExpiredJwtException e)
        {
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        String category=jwtUtil.getCategory(refresh);

        if(!category.equals("Refresh"))
        {
            return new ResponseEntity<>("invaild refresh token", HttpStatus.BAD_REQUEST);
        }
        String userId=jwtUtil.getUserId(refresh);
        String userName=jwtUtil.getUserName(refresh);
        System.out.println(userName);
        String role =jwtUtil.getRole(refresh);

        refreshRepository.deleteByRefresh(refresh);


        String newAccess =jwtUtil.createJwt("Authorization", userId,userName,role,600000L);
        String newRefresh = jwtUtil.createJwt("Refresh",userId,userName, role, 86400000L);


        updateOrInsertRefreshEntity(userId,userName,newRefresh,86400000L);
        updateOrInsertAccessEntity(userId,userName, NetworkUtils.getLocalIpAddress(),newAccess,600000L);

        response.setHeader("Authorization",newAccess);
        response.addCookie(createCookie("Refresh", newRefresh));

        return new ResponseEntity<>(HttpStatus.OK);
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

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        //cookie.setSecure(true);
        //cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
