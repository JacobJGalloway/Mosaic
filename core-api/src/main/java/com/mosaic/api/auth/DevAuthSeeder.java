package com.mosaic.api.auth;

import com.mosaic.domain.auth.ActionIds;
import com.mosaic.domain.auth.Role;
import com.mosaic.domain.auth.RoleService;
import com.mosaic.domain.auth.User;
import com.mosaic.domain.auth.UserRepository;
import com.mosaic.domain.auth.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Dev-only bootstrap: with an empty users/roles collection there would be
 * no way to log in and create the first user via the API (registration
 * requires a roleId, and nothing can call the protected admin endpoints
 * without a token in the first place). Seeds one ADMIN role with every
 * known action ID and one admin user, idempotently, only when both
 * collections are empty. Not meant to survive past Sprint 2 as-is —
 * revisit once there's a real onboarding flow.
 */
@Component
public class DevAuthSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DevAuthSeeder.class);
    private static final String DEV_ADMIN_USERNAME = "admin";
    private static final String DEV_ADMIN_PASSWORD = "changeme123";

    private final RoleService roleService;
    private final UserService userService;
    private final UserRepository userRepository;

    public DevAuthSeeder(RoleService roleService, UserService userService, UserRepository userRepository) {
        this.roleService = roleService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.findAll().isEmpty()) {
            return;
        }

        Role adminRole = roleService.findByName("ADMIN")
                .orElseGet(() -> roleService.createRole("ADMIN", Set.of(
                        ActionIds.CLIENT_CREATE,
                        ActionIds.CLIENT_READ,
                        ActionIds.POLICY_CREATE,
                        ActionIds.POLICY_READ,
                        ActionIds.USER_MANAGE)));

        User admin = userService.register(DEV_ADMIN_USERNAME, DEV_ADMIN_PASSWORD, adminRole.getId());
        log.warn("Seeded dev ADMIN user '{}' (id={}) with password '{}' — change or remove before any real deployment",
                admin.getUsername(), admin.getId(), DEV_ADMIN_PASSWORD);
    }
}
