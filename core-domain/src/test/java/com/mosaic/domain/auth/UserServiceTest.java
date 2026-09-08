package com.mosaic.domain.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, roleRepository, passwordEncoder);
        lenient().when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private User userWith(String id, boolean active, long tokenVersion) {
        User user = new User();
        user.setId(id);
        user.setActive(active);
        user.setTokenVersion(tokenVersion);
        return user;
    }

    // ── register ────────────────────────────────────────────────────────

    @Test
    void registerHashesPasswordAndSavesUser() {
        when(userRepository.findByUsername("jane")).thenReturn(Optional.empty());
        when(roleRepository.findById("role-1")).thenReturn(Optional.of(new Role()));
        when(passwordEncoder.encode("secret")).thenReturn("hashed-secret");

        User created = userService.register("jane", "secret", "role-1");

        assertThat(created.getUsername()).isEqualTo("jane");
        assertThat(created.getPasswordHash()).isEqualTo("hashed-secret");
        assertThat(created.getRoleId()).isEqualTo("role-1");
        assertThat(created.getCreatedAt()).isNotNull();
    }

    @Test
    void registerRejectsDuplicateUsername() {
        when(userRepository.findByUsername("jane")).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> userService.register("jane", "secret", "role-1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("jane");
    }

    @Test
    void registerRejectsUnknownRole() {
        when(userRepository.findByUsername("jane")).thenReturn(Optional.empty());
        when(roleRepository.findById("bogus-role")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.register("jane", "secret", "bogus-role"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── resolveActions ──────────────────────────────────────────────────

    @Test
    void resolveActionsUnionsRoleAndAddsMinusRemoves() {
        Role role = new Role();
        role.setActionIds(new java.util.HashSet<>(Set.of("CLIENT_READ", "POLICY_READ")));
        User user = new User();
        user.setRoleId("role-1");
        user.setActionOverridesAdd(Set.of("USER_MANAGE"));
        user.setActionOverridesRemove(Set.of("POLICY_READ"));
        when(roleRepository.findById("role-1")).thenReturn(Optional.of(role));

        Set<String> resolved = userService.resolveActions(user);

        assertThat(resolved).containsExactlyInAnyOrder("CLIENT_READ", "USER_MANAGE");
    }

    @Test
    void resolveActionsRejectsUnknownRole() {
        User user = new User();
        user.setRoleId("missing-role");
        when(roleRepository.findById("missing-role")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.resolveActions(user))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── updateActionOverrides ───────────────────────────────────────────

    @Test
    void updateActionOverridesReplacesSetsAndBumpsTokenVersion() {
        User user = userWith("user-1", true, 5);
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));

        User updated = userService.updateActionOverrides("user-1", Set.of("A"), Set.of("B"));

        assertThat(updated.getActionOverridesAdd()).containsExactly("A");
        assertThat(updated.getActionOverridesRemove()).containsExactly("B");
        assertThat(updated.getTokenVersion()).isEqualTo(6);
    }

    // ── deactivate ──────────────────────────────────────────────────────

    @Test
    void deactivateSucceedsWhenAnotherActiveUserExists() {
        User target = userWith("user-1", true, 3);
        User other = userWith("user-2", true, 0);
        when(userRepository.findById("user-1")).thenReturn(Optional.of(target));
        when(userRepository.findByActiveTrue()).thenReturn(List.of(target, other));

        User result = userService.deactivate("user-1");

        assertThat(result.isActive()).isFalse();
        assertThat(result.getTokenVersion()).isEqualTo(4);
    }

    @Test
    void deactivateRejectsLastActiveUser() {
        User target = userWith("user-1", true, 3);
        when(userRepository.findById("user-1")).thenReturn(Optional.of(target));
        when(userRepository.findByActiveTrue()).thenReturn(List.of(target));

        assertThatThrownBy(() -> userService.deactivate("user-1"))
                .isInstanceOf(IllegalStateException.class);
        assertThat(target.isActive()).isTrue();
        assertThat(target.getTokenVersion()).isEqualTo(3);
    }

    @Test
    void deactivateOnAlreadyInactiveUserIsANoOpAndDoesNotBumpTokenVersion() {
        User target = userWith("user-1", false, 3);
        when(userRepository.findById("user-1")).thenReturn(Optional.of(target));

        User result = userService.deactivate("user-1");

        assertThat(result.getTokenVersion()).isEqualTo(3);
        verify(userRepository, never()).save(any(User.class));
        verify(userRepository, never()).findByActiveTrue();
    }

    // ── reactivate ──────────────────────────────────────────────────────

    @Test
    void reactivateSetsActiveTrueWithoutTouchingTokenVersion() {
        User target = userWith("user-1", false, 4);
        when(userRepository.findById("user-1")).thenReturn(Optional.of(target));

        User result = userService.reactivate("user-1");

        assertThat(result.isActive()).isTrue();
        assertThat(result.getTokenVersion()).isEqualTo(4);
    }

    @Test
    void deactivateRejectsUnknownUser() {
        when(userRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deactivate("missing"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
