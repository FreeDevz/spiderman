package com.todoapp.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Utility class for JWT token operations.
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret:mySecretKey}")
    private String jwtSecret;

    @Value("${jwt.refresh-secret:myRefreshSecretKey}")
    private String refreshSecret;

    @Value("${jwt.expiration:1800000}") // 30 minutes default
    private int jwtExpirationInMs;

    @Value("${jwt.refresh-expiration:604800000}") // 7 days default
    private int refreshTokenExpirationInMs;

    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private final SecretKey refreshSecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    /**
     * Generate JWT token for user.
     * @param email User email
     * @return JWT token
     */
    public String generateToken(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationInMs))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Generate refresh token for user.
     * @param email User email
     * @return Refresh token
     */
    public String generateRefreshToken(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + refreshTokenExpirationInMs))
                .signWith(refreshSecretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Get email from JWT token.
     * @param token JWT token
     * @return User email
     */
    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Get email from refresh token.
     * @param token Refresh token
     * @return User email
     */
    public String getEmailFromRefreshToken(String token) {
        return Jwts.parser()
                .verifyWith(refreshSecretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Validate JWT token.
     * @param token JWT token
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validate refresh token.
     * @param token Refresh token
     * @return true if valid, false otherwise
     */
    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(refreshSecretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get token expiration date.
     * @param token JWT token
     * @return Expiration date
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getExpiration();
    }

    /**
     * Check if token is expired.
     * @param token JWT token
     * @return true if expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Get JWT expiration time in milliseconds.
     * @return Expiration time
     */
    public long getExpirationInMs() {
        return jwtExpirationInMs;
    }
} 