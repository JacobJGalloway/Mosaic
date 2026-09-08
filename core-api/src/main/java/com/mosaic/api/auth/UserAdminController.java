package com.mosaic.api.auth;

import com.mosaic.domain.auth.ActionIds;
import com.mosaic.domain.auth.User;
import com.mosaic.domain.auth.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

/**
 * All operations here are the "destructive/security-relevant service-layer
 * action" case from ARCHITECTURE.md's Auth Design: each one bumps the
 * affected user's tokenVersion, invalidating their currently-held tokens
 * immediately rather than at natural expiry — including a caller editing
 * or deactivating their own account, which falls out of UserService's
 * "every user whose resolved actions changed" rule with no special-casing.
 */
@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasAuthority('" + ActionIds.USER_MANAGE + "')")
public class UserAdminController {

    private final UserService userService;

    public UserAdminController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/{id}/overrides")
    public User updateOverrides(@PathVariable String id, @Valid @RequestBody UserOverridesRequest request) {
        try {
            return userService.updateActionOverrides(
                    id,
                    request.actionOverridesAdd() == null ? Set.of() : request.actionOverridesAdd(),
                    request.actionOverridesRemove() == null ? Set.of() : request.actionOverridesRemove());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/{id}/deactivate")
    public User deactivate(@PathVariable String id) {
        try {
            return userService.deactivate(id);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PostMapping("/{id}/reactivate")
    public User reactivate(@PathVariable String id) {
        try {
            return userService.reactivate(id);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
