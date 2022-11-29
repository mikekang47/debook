package com.sihoo.me.debook.utils;

import com.sihoo.me.debook.errors.CustomException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtUtil {
    private final Key key;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String encode(Long id) {
        return Jwts.builder()
                .signWith(key)
                .setHeaderParam("type", "jwt")
                .claim("userId", id)
                .compact();
    }

    public Claims decode(String token) {
        if(token == null || token.isBlank()) {
            throw new CustomException("[ERROR] Invalid Token(token :" + token + ")", HttpStatus.BAD_REQUEST);
        }
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SignatureException e) {
            throw new CustomException("[ERROR] Invalid Token(token :" + token + ")", HttpStatus.BAD_REQUEST);
        }
    }
}
