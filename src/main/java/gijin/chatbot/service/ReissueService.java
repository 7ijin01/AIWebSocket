package gijin.chatbot.service;


import gijin.chatbot.jwt.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ReissueService
{
    private final JWTUtil jwtUtil;

    public ReissueService(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
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
        String role =jwtUtil.getRole(refresh);

        String newAccess =jwtUtil.createJwt("access", userId,userName,role,600000L);
        String newRefresh = jwtUtil.createJwt("refresh",userId,userName, role, 86400000L);
        response.setHeader("Authorization",newAccess);
        response.addCookie(createCookie("Refresh", newRefresh));
        return new ResponseEntity<>(HttpStatus.OK);




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
