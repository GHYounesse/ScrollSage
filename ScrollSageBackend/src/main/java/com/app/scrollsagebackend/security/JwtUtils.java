package com.app.scrollsagebackend.security;

import com.app.scrollsagebackend.config.EnvConfig;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
//JWT Utility Class
@Component
public class JwtUtils {
//    @Value("${jwt.secret}")
//    private String secret;
//
//    @Value("${jwt.expiration}")
//    private long jwtExpiration;
    private final String secret = EnvConfig.dotenv.get("JWT_SECRET");
    private final long jwtExpiration = Long.parseLong(EnvConfig.dotenv.get("JWT_EXPIRATION"));
    // Generate token with the secret and expiration
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder().setSubject(userDetails.getUsername()).setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis()+ jwtExpiration))
            .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256).compact();
    }
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(secret.getBytes()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }
    private boolean isExpired(String token) {
        Date exp = Jwts.parserBuilder().setSigningKey(secret.getBytes()).build().parseClaimsJws(token).getBody().getExpiration();
        return exp.before(new Date());
    }
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = getUsernameFromToken(token);
        return username.equals(userDetails.getUsername()) && !isExpired(token);
    }
}
