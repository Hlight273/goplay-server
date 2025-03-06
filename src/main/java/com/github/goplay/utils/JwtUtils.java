package com.github.goplay.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


import java.util.Date;

public class JwtUtils {

    // TOKEN的有效期 三天
    //private static final int TOKEN_TIME_OUT = (int)(60*60*24*3);
    private static final int TOKEN_TIME_OUT = (int)(60*60*24*3);

    // 加密KEY
    private static final String TOKEN_SECRET = "1145141919810xd";


    public static String generateToken(Integer userId, String username){
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 1000*TOKEN_TIME_OUT);
        long timestamp = System.currentTimeMillis() / 1000; // 增加当前时间戳
        return  Jwts.builder()
                .setHeaderParam("type","JWT")
                .setSubject(username)
                .setIssuedAt(now)
                .claim("userId", userId)
                .claim("timestamp", timestamp) // 添加时间戳到payload
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS512, TOKEN_SECRET)
                .compact();
    }

    public static Claims getClaimsByToken(String token){
        return Jwts.parser()
                .setSigningKey(TOKEN_SECRET)
                .parseClaimsJws(token)
                .getBody();
    }

    ///jwt中解析用户Id，用于查询请求的真正用户Id
    public static Integer getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(TOKEN_SECRET)
                .parseClaimsJws(token)
                .getBody();

        return claims.get("userId", Integer.class);
    }
}
