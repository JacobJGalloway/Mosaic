package com.mosaic.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String SECRET = "test-only-secret-at-least-32-bytes-long-for-hs256";

    private final JwtService jwtService = new JwtService(SECRET);

    @Test
    void issuedTokenRoundTripsSubjectActionsAndTokenVersion() {
        String token = jwtService.issueToken("user-1", Set.of("CLIENT_READ", "POLICY_READ"), 3L);

        Claims claims = jwtService.parseAndValidate(token);

        assertThat(claims.getSubject()).isEqualTo("user-1");
        assertThat(jwtService.extractActions(claims)).containsExactlyInAnyOrder("CLIENT_READ", "POLICY_READ");
        assertThat(jwtService.extractTokenVersion(claims)).isEqualTo(3L);
    }

    @Test
    void tokenSignedWithADifferentSecretFailsValidation() {
        JwtService otherServer = new JwtService("a-completely-different-secret-also-32-bytes-plus");
        String token = otherServer.issueToken("user-1", Set.of("CLIENT_READ"), 0L);

        assertThatThrownBy(() -> jwtService.parseAndValidate(token))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void expiredTokenFailsValidation() {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());
        Instant past = Instant.now().minusSeconds(3600);
        String expiredToken = Jwts.builder()
                .subject("user-1")
                .claim("actions", List.of("CLIENT_READ"))
                .claim("tokenVersion", 0L)
                .issuedAt(Date.from(past.minusSeconds(60)))
                .expiration(Date.from(past))
                .signWith(key)
                .compact();

        assertThatThrownBy(() -> jwtService.parseAndValidate(expiredToken))
                .isInstanceOf(ExpiredJwtException.class);
    }
}
