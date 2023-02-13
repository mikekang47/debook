package com.sihoo.me.debook.utils;

import java.security.Key;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sihoo.me.debook.errors.InvalidTokenException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

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
		if (token == null || token.isBlank()) {
			throw new InvalidTokenException(token);
		}
		try {
			return Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody();
		} catch (SignatureException e) {
			throw new InvalidTokenException(token);
		}
	}
}
