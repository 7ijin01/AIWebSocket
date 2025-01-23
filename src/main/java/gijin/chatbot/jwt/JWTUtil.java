package gijin.chatbot.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil
{
    private SecretKey secretkey;

    public JWTUtil(@Value("${spring.jwt.secret}")String key) {
        this.secretkey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }
    public String getCategory(String token) {

        return Jwts.parser().verifyWith(secretkey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }
    public String getUserId(String token)
    {
        return Jwts.parser().verifyWith(secretkey).build().parseSignedClaims(token).getPayload().get("userId",String.class);
    }
    public String getRole(String token)
    {
        return Jwts.parser().verifyWith(secretkey).build().parseSignedClaims(token).getPayload().get("role",String.class);
    }
    public String getUserName(String token)
    {
        return Jwts.parser().verifyWith(secretkey).build().parseSignedClaims(token).getPayload().get("username",String.class);
    }
    public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretkey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }
    public String createJwt(String category,String userId, String role,String userName,Long expiredMS)
    {
        return Jwts.builder()
                .claim("category",category)
                .claim("userid",userId)
                .claim("username",userName)
                .claim("role",role)
                .expiration(new Date(System.currentTimeMillis()+expiredMS))
                .signWith(secretkey)
                .compact();
    }
}
