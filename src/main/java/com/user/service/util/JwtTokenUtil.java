package com.user.service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtTokenUtil {
    // You should keep this secret key secure and not hardcode it in a real-world scenario
    private static final String SECRET_KEY = "your_secret_key_here";

    // Token expiration time in milliseconds (1 hour in this example)
    private static final long EXPIRATION_TIME = 3600000;

    // Generate a JWT token
    public static String generateToken(String subject) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION_TIME);

        SecretKey secretKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // Validate a JWT token
    public static boolean validateToken(String token) {
        try {
            SecretKey secretKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);

            return true;
        } catch (Exception e) {
            // If there's an exception, the token is not valid
            return false;
        }
    }

    // Get the subject (usually user ID) from a valid token
    public static String getSubjectFromToken(String token) {
        SecretKey secretKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }
}
