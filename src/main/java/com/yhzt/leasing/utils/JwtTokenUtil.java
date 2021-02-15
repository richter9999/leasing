package com.yhzt.leasing.utils;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Component
public class JwtTokenUtil implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    
    private static final String CLAIM_KEY_USERNAME = "sub";
    //private static final String CLAIM_KEY_ID = "id";
    private static final String CLAIM_KEY_CREATED = "created";
    //private static final String CLAIM_KEY_ROLES = "roles";

    private static String secret = "secret"; // 密钥

    private static long expiration = 36000000; // 过期时间 单位为秒

    //private static String header = "Authorization";

    /**
     * 根据 userDetails 生成 令牌 Token
     * 
     * @param claims
     * @return
     */
    private static String generateToken(Map<String, Object> claims) {
        log.info("读取的信息 : " + secret);
        return Jwts.builder().setClaims(claims).setExpiration(new Date(Instant.now().toEpochMilli() + expiration))
                .signWith(SignatureAlgorithm.HS512, secret).compact();

    }

    /**
     * 根绝 Token 获取数据声明
     * 
     * @param token
     * @return
     */
    private static Claims getClaimsFromToken(String token) {
        Claims claims = null;

        claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();

        return claims;
    }

    /**
     * 生成 Token
     * 
     * @param username
     * @return
     */
    public static String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>(2);
        claims.put(CLAIM_KEY_USERNAME, username);
        claims.put(CLAIM_KEY_CREATED, new Date());

        String token = generateToken(claims);

        log.debug("生成的 Token :  {}", token);

        return token;

    }

    /**
     * 根绝 Token 获取 userName
     * 
     * @param token
     * @return
     */
    public static String getUsernameFromToken(String token) {
        String userName = "";
        Claims claims = getClaimsFromToken(token);
        userName = claims.getSubject();

        return userName;
    }

    /**
     * 判断 Token 是否过期
     * 
     * @param token
     * @return
     */
    public static Boolean isTokenExpired(String token) {
        Claims claims = getClaimsFromToken(token);
        Date expiration = claims.getExpiration();

        return expiration.before(new Date());
    }

    /**
     * 刷新 Token
     * 
     * @param token
     * @return
     */
    public static String refreshToken(String token) {
        String refreshedToken = "";

        Claims claims = getClaimsFromToken(token);
        claims.put(CLAIM_KEY_CREATED, new Date());
        refreshedToken = generateToken(claims);

        return refreshedToken;
    }

    /**
     * 验证 Token 是否合法
     * 
     * @param token
     * @param userDetails
     * @return
     */
    public static Boolean validateToken(String token, UserDetails userDetails) {
        String userName = getUsernameFromToken(token);

        return userDetails.getUsername().equals(userName);
    }
}