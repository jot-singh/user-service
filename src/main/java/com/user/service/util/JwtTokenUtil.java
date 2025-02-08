package com.user.service.util;

import java.util.Date;
import java.util.Objects;

import javax.crypto.SecretKey;

import com.user.service.error.InvalidCredentialsException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtTokenUtil {
    // You should keep this secret key secure and not hardcode it in a real-world scenario
    private static final String SECRET_KEY = Jwts.SIG.HS256.key().build().toString();
    private static final SecretKey secretKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // Token expiration time in milliseconds (1 hour in this example)
    private static final long EXPIRATION_TIME = 3600000;

    // Generate a JWT token
    public static String generateToken(String subject) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION_TIME);
        System.out.println("Generating token for subject: " + subject);
        return Jwts.builder()
                .subject(subject)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    // Validate a JWT token
    public static boolean validateToken(String token) throws InvalidCredentialsException {
    
        try {
            Objects.requireNonNull(token, "Token is required");
            Jws<Claims> claims = getClaimsFromToken(token);
            Claims payload = claims.getPayload();
            return isValidToken(payload) && !isTokenExpired(payload) 
            && isUserIdPresent(payload);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isValidToken(Claims payload) {
        return payload != null;
    }

    private static boolean isTokenExpired(Claims payload) {
        return payload.getExpiration().before(new Date());
    }   

    private static boolean isUserIdPresent(Claims payload) {
        return Objects.nonNull(payload.getSubject());
    }

    // Get the subject (usually user ID) from a valid token
    public static String getSubjectFromToken(String token) {
        return getClaimsFromToken(token).getPayload().getSubject();
    }

    public static Jws<Claims> getClaimsFromToken(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
    }
}
