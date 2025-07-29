package com.todoapp.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_SECRET = "testSecretKeyForTestingPurposesOnly";
    private static final String TEST_REFRESH_SECRET = "testRefreshSecretKeyForTestingPurposesOnly";
    private static final int TEST_EXPIRATION = 1800000; // 30 minutes
    private static final int TEST_REFRESH_EXPIRATION = 604800000; // 7 days

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtUtil, "refreshSecret", TEST_REFRESH_SECRET);
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationInMs", TEST_EXPIRATION);
        ReflectionTestUtils.setField(jwtUtil, "refreshTokenExpirationInMs", TEST_REFRESH_EXPIRATION);
    }

    @Test
    void generateToken_WithValidEmail_ShouldReturnValidToken() {
        // When
        String token = jwtUtil.generateToken(TEST_EMAIL);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts: header.payload.signature
    }

    @Test
    void generateToken_WithNullEmail_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> jwtUtil.generateToken(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void generateToken_WithEmptyEmail_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> jwtUtil.generateToken(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void generateRefreshToken_WithValidEmail_ShouldReturnValidToken() {
        // When
        String refreshToken = jwtUtil.generateRefreshToken(TEST_EMAIL);

        // Then
        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken).isNotEmpty();
        assertThat(refreshToken.split("\\.")).hasSize(3);
    }

    @Test
    void generateRefreshToken_WithNullEmail_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> jwtUtil.generateRefreshToken(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getEmailFromToken_WithValidToken_ShouldReturnEmail() {
        // Given
        String token = jwtUtil.generateToken(TEST_EMAIL);

        // When
        String email = jwtUtil.getEmailFromToken(token);

        // Then
        assertThat(email).isEqualTo(TEST_EMAIL);
    }

    @Test
    void getEmailFromToken_WithInvalidToken_ShouldThrowException() {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        assertThatThrownBy(() -> jwtUtil.getEmailFromToken(invalidToken))
                .isInstanceOf(Exception.class);
    }

    @Test
    void getEmailFromToken_WithNullToken_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> jwtUtil.getEmailFromToken(null))
                .isInstanceOf(Exception.class);
    }

    @Test
    void getEmailFromRefreshToken_WithValidToken_ShouldReturnEmail() {
        // Given
        String refreshToken = jwtUtil.generateRefreshToken(TEST_EMAIL);

        // When
        String email = jwtUtil.getEmailFromRefreshToken(refreshToken);

        // Then
        assertThat(email).isEqualTo(TEST_EMAIL);
    }

    @Test
    void getEmailFromRefreshToken_WithInvalidToken_ShouldThrowException() {
        // Given
        String invalidToken = "invalid.refresh.token.here";

        // When & Then
        assertThatThrownBy(() -> jwtUtil.getEmailFromRefreshToken(invalidToken))
                .isInstanceOf(Exception.class);
    }

    @Test
    void getEmailFromRefreshToken_WithNullToken_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> jwtUtil.getEmailFromRefreshToken(null))
                .isInstanceOf(Exception.class);
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        // Given
        String token = jwtUtil.generateToken(TEST_EMAIL);

        // When
        boolean isValid = jwtUtil.validateToken(token);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    void validateToken_WithInvalidToken_ShouldReturnFalse() {
        // Given
        String invalidToken = "invalid.token.here";

        // When
        boolean isValid = jwtUtil.validateToken(invalidToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void validateToken_WithNullToken_ShouldReturnFalse() {
        // When
        boolean isValid = jwtUtil.validateToken(null);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void validateRefreshToken_WithValidToken_ShouldReturnTrue() {
        // Given
        String refreshToken = jwtUtil.generateRefreshToken(TEST_EMAIL);

        // When
        boolean isValid = jwtUtil.validateRefreshToken(refreshToken);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    void validateRefreshToken_WithInvalidToken_ShouldReturnFalse() {
        // Given
        String invalidToken = "invalid.refresh.token.here";

        // When
        boolean isValid = jwtUtil.validateRefreshToken(invalidToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void validateRefreshToken_WithNullToken_ShouldReturnFalse() {
        // When
        boolean isValid = jwtUtil.validateRefreshToken(null);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void getExpirationDateFromToken_WithValidToken_ShouldReturnExpirationDate() {
        // Given
        String token = jwtUtil.generateToken(TEST_EMAIL);

        // When
        Date expirationDate = jwtUtil.getExpirationDateFromToken(token);

        // Then
        assertThat(expirationDate).isNotNull();
        assertThat(expirationDate).isAfter(new Date());
    }

    @Test
    void getExpirationDateFromToken_WithInvalidToken_ShouldThrowException() {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        assertThatThrownBy(() -> jwtUtil.getExpirationDateFromToken(invalidToken))
                .isInstanceOf(Exception.class);
    }

    @Test
    void isTokenExpired_WithValidToken_ShouldReturnFalse() {
        // Given
        String token = jwtUtil.generateToken(TEST_EMAIL);

        // When
        boolean isExpired = jwtUtil.isTokenExpired(token);

        // Then
        assertThat(isExpired).isFalse();
    }

    @Test
    void isTokenExpired_WithExpiredToken_ShouldReturnTrue() {
        // Given - Create a token with very short expiration
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationInMs", 1); // 1ms
        String token = jwtUtil.generateToken(TEST_EMAIL);
        
        // Wait for token to expire
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When
        boolean isExpired = jwtUtil.isTokenExpired(token);

        // Then
        assertThat(isExpired).isTrue();
    }

    @Test
    void isTokenExpired_WithInvalidToken_ShouldThrowException() {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        assertThatThrownBy(() -> jwtUtil.isTokenExpired(invalidToken))
                .isInstanceOf(Exception.class);
    }

    @Test
    void getExpirationInMs_ShouldReturnCorrectValue() {
        // When
        long expirationInMs = jwtUtil.getExpirationInMs();

        // Then
        assertThat(expirationInMs).isEqualTo(TEST_EXPIRATION);
    }

    @Test
    void tokenAndRefreshToken_ShouldBeDifferent() {
        // When
        String token = jwtUtil.generateToken(TEST_EMAIL);
        String refreshToken = jwtUtil.generateRefreshToken(TEST_EMAIL);

        // Then
        assertThat(token).isNotEqualTo(refreshToken);
    }

    @Test
    void tokenFromDifferentEmails_ShouldBeDifferent() {
        // When
        String token1 = jwtUtil.generateToken("user1@example.com");
        String token2 = jwtUtil.generateToken("user2@example.com");

        // Then
        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    void tokenStructure_ShouldHaveCorrectFormat() {
        // When
        String token = jwtUtil.generateToken(TEST_EMAIL);
        String[] parts = token.split("\\.");

        // Then
        assertThat(parts).hasSize(3);
        assertThat(parts[0]).isNotEmpty(); // Header
        assertThat(parts[1]).isNotEmpty(); // Payload
        assertThat(parts[2]).isNotEmpty(); // Signature
    }

    @Test
    void refreshTokenStructure_ShouldHaveCorrectFormat() {
        // When
        String refreshToken = jwtUtil.generateRefreshToken(TEST_EMAIL);
        String[] parts = refreshToken.split("\\.");

        // Then
        assertThat(parts).hasSize(3);
        assertThat(parts[0]).isNotEmpty(); // Header
        assertThat(parts[1]).isNotEmpty(); // Payload
        assertThat(parts[2]).isNotEmpty(); // Signature
    }
} 