package com.lab.global.security;

import com.lab.user.entity.AppUser;
import com.lab.user.entity.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProvider {
    @Value("${jwt.secret}") private String secret;
    @Value("${jwt.expiration-ms}") private long expirationMs;
    private SecretKey key;

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(AppUser user) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);

        JwtBuilder builder = Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim("userId", user.getUserId())
                .claim("name", user.getName())
                .claim("role", user.getRole().name())
                .setIssuedAt(now)
                .setExpiration(exp);

        if (user.getDepartment() != null) {
            builder.claim("departmentId", user.getDepartment().getId());
            builder.claim("departmentName", user.getDepartment().getName());
        }

        return builder.signWith(key).compact();
    }

    public CustomUserPrincipal parseToken(String token) {
        Claims c = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        Object departmentIdValue = c.get("departmentId");
        Long departmentId = null;
        if (departmentIdValue instanceof Number number) {
            departmentId = number.longValue();
        }
        return new CustomUserPrincipal(
                Long.valueOf(c.getSubject()),
                c.get("userId", String.class),
                c.get("name", String.class),
                UserRole.valueOf(c.get("role", String.class)),
                departmentId,
                c.get("departmentName", String.class)
        );
    }
}
