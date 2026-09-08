package com.mosaic.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Issues and verifies the JWT described in ARCHITECTURE.md's Auth Design.
 * The "signature" in that design is this token's own cryptographic
 * signature (HMAC via jjwt) — it's what makes the {@code actions} claim
 * tamper-evident, not a separate secret.
 */
@Component
public class JwtService {

    private static final String ACTIONS_CLAIM = "actions";
    private static final String TOKEN_VERSION_CLAIM = "tokenVersion";

    private static final Duration TOKEN_TTL = Duration.ofMinutes(30);

    private final SecretKey signingKey;

    public JwtService(@Value("${mosaic.auth.jwt-secret}") String jwtSecret) {
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String issueToken(String userId, Set<String> resolvedActions, long tokenVersion) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(userId)
                .claim(ACTIONS_CLAIM, List.copyOf(resolvedActions))
                .claim(TOKEN_VERSION_CLAIM, tokenVersion)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(TOKEN_TTL)))
                .signWith(signingKey)
                .compact();
    }

    /**
     * Verifies signature and expiry, then returns the decoded claims.
     * Throws an unchecked jjwt exception (caught by the security filter)
     * if the token is malformed, expired, or its signature doesn't verify.
     */
    public Claims parseAndValidate(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @SuppressWarnings("unchecked")
    public List<String> extractActions(Claims claims) {
        return (List<String>) claims.get(ACTIONS_CLAIM, List.class);
    }

    public long extractTokenVersion(Claims claims) {
        return claims.get(TOKEN_VERSION_CLAIM, Long.class);
    }
}
