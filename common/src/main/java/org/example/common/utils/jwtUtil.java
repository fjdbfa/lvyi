package org.example.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class jwtUtil {

    public static String buildToken(Map<String,Object> x,Long ttl,String adminSecretKey) {

        return Jwts.builder().signWith(SignatureAlgorithm.HS256, adminSecretKey.getBytes(StandardCharsets.UTF_8))
                .addClaims(x)//添加自定义信息
                .setExpiration(new Date(System.currentTimeMillis() + ttl))//设置过期时间
                .compact();

    }

    public static Claims parseToken(String token, String adminSecretKey) {
        return Jwts
                .parser()
                .setSigningKey(adminSecretKey.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token).getBody();
    }
//这个Claims就是一个Map<String,Object>，可以通过get方法获取自定义信息
}
