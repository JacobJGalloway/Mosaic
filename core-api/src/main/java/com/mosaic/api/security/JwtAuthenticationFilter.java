package com.mosaic.api.security;

import com.mosaic.domain.auth.User;
import com.mosaic.domain.auth.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Implements ARCHITECTURE.md's three-part protected-request check:
 * (1) JWT signature + expiry valid, (2) the token's tokenVersion claim
 * matches the user's current stored tokenVersion (the forced-refresh
 * mechanism), (3) — left to method-level @PreAuthorize("hasAuthority(...)")
 * checks against the authorities this filter derives from the token's
 * actions claim.
 * <p>
 * Any failure here simply leaves the request unauthenticated rather than
 * short-circuiting the response directly — Spring Security's normal
 * access-denied handling then applies to whichever endpoints require
 * authentication/authority.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            authenticate(header.substring("Bearer ".length()));
        }
        filterChain.doFilter(request, response);
    }

    private void authenticate(String token) {
        try {
            Claims claims = jwtService.parseAndValidate(token);
            String userId = claims.getSubject();
            long tokenVersion = jwtService.extractTokenVersion(claims);

            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return;
            }
            User user = userOpt.get();
            if (!user.isActive() || user.getTokenVersion() != tokenVersion) {
                return;
            }

            List<String> actions = jwtService.extractActions(claims);
            List<GrantedAuthority> authorities = actions.stream()
                    .map(SimpleGrantedAuthority::new)
                    .map(GrantedAuthority.class::cast)
                    .toList();

            var authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JwtException | IllegalArgumentException e) {
            // Malformed/expired/invalid token: leave the request unauthenticated.
        }
    }
}
