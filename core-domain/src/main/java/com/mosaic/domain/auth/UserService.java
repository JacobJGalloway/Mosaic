package com.mosaic.domain.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String username, String rawPassword, String roleId) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already taken: " + username);
        }
        roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("No role found with id " + roleId));

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setRoleId(roleId);
        Instant now = Instant.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    /**
     * Resolved action-ID set for a user: their role's baseline actions,
     * plus their own additions, minus their own removals. This is computed
     * fresh at both login and refresh — never cached across a token's
     * lifetime — per ARCHITECTURE.md's Auth Design.
     */
    public Set<String> resolveActions(User user) {
        Role role = roleRepository.findById(user.getRoleId())
                .orElseThrow(() -> new IllegalArgumentException("No role found with id " + user.getRoleId()));
        Set<String> resolved = new HashSet<>(role.getActionIds());
        resolved.addAll(user.getActionOverridesAdd());
        resolved.removeAll(user.getActionOverridesRemove());
        return resolved;
    }

    /**
     * Replaces a user's permission overrides and immediately bumps their
     * {@code tokenVersion}, invalidating any currently-held tokens on their
     * next request rather than waiting for expiry — this covers a
     * super-admin editing another user's permissions, or their own.
     */
    public User updateActionOverrides(String userId, Set<String> actionOverridesAdd, Set<String> actionOverridesRemove) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("No user found with id " + userId));
        user.setActionOverridesAdd(new HashSet<>(actionOverridesAdd));
        user.setActionOverridesRemove(new HashSet<>(actionOverridesRemove));
        user.setTokenVersion(user.getTokenVersion() + 1);
        user.setUpdatedAt(Instant.now());
        return userRepository.save(user);
    }

    /**
     * Soft-deletes a user. Guarded so the last remaining active user can
     * never be deactivated — otherwise no one would be left to reactivate
     * anyone. Bumps tokenVersion so any tokens the user is still holding
     * are rejected on their very next request.
     */
    public User deactivate(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("No user found with id " + userId));
        if (!user.isActive()) {
            return user;
        }
        long otherActiveUsers = userRepository.findByActiveTrue().stream()
                .filter(u -> !u.getId().equals(userId))
                .count();
        if (otherActiveUsers == 0) {
            throw new IllegalStateException("Cannot deactivate the last remaining active user");
        }
        user.setActive(false);
        user.setTokenVersion(user.getTokenVersion() + 1);
        user.setUpdatedAt(Instant.now());
        return userRepository.save(user);
    }

    public User reactivate(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("No user found with id " + userId));
        user.setActive(true);
        user.setUpdatedAt(Instant.now());
        return userRepository.save(user);
    }
}
