package com.mosaic.domain.auth;

import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role createRole(String name, Set<String> actionIds) {
        Role role = new Role();
        role.setName(name);
        role.setActionIds(actionIds);
        return roleRepository.save(role);
    }

    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }
}
