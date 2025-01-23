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
    /**
     * Jwts.parser() -> Jwts parser 생성
     * .verifyWith(secretKey) -> 토큰 검증에 사용할 비밀 키 설정 토큰 위 변조 검증이 이루어짐
     * .build() 파서 구성을 완료하고, 검증을 위한 파서 객체를 생성
     * .parseSignedClaims(token) JWT의 서명된 클레임(payload)을 추출하는 메서드
     * .getPayload() 파싱된 클레임 객체에서 payload 부분을 가져옴
     * .get("email", String.class)는 JWT의 페이로드에서 "email"이라는 이름의 클레임을 String 타입으로 가져엄
     */
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
