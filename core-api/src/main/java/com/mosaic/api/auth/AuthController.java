package com.mosaic.api.auth;

import com.mosaic.api.security.JwtService;
import com.mosaic.domain.auth.User;
import com.mosaic.domain.auth.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = userService.register(request.username(), request.password(), request.roleId());
            return ResponseEntity.status(HttpStatus.CREATED).body(issueResponse(user));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        User user = userService.findByUsername(request.username())
                .filter(User::isActive)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        return issueResponse(user);
    }

    /**
     * Re-resolves the caller's actions fresh from the current role/override
     * state and issues a new token — the mechanism by which ordinary
     * permission drift (promotions, demotions, training completions)
     * propagates without requiring re-login.
     */
    @PostMapping("/refresh")
    public AuthResponse refresh(Authentication authentication) {
        if (!(authentication instanceof UsernamePasswordAuthenticationToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid token");
        }
        String userId = authentication.getName();
        User user = userService.findById(userId)
                .filter(User::isActive)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User no longer active"));
        return issueResponse(user);
    }

    private AuthResponse issueResponse(User user) {
        var actions = userService.resolveActions(user);
        String token = jwtService.issueToken(user.getId(), actions, user.getTokenVersion());
        return new AuthResponse(token, user.getId());
    }
}
