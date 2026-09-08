package com.mosaic.api.policy;

import com.mosaic.domain.auth.ActionIds;
import com.mosaic.domain.policy.Policy;
import com.mosaic.domain.policy.PolicyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('" + ActionIds.POLICY_CREATE + "')")
    public ResponseEntity<Policy> createPolicy(@Valid @RequestBody Policy policy) {
        Policy created = policyService.createPolicy(policy);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('" + ActionIds.POLICY_READ + "')")
    public Policy fetchPolicy(@PathVariable String id) {
        return policyService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No policy found with id " + id));
    }

    @GetMapping("/client/{clientId}")
    @PreAuthorize("hasAuthority('" + ActionIds.POLICY_READ + "')")
    public List<Policy> fetchPoliciesForClient(@PathVariable String clientId) {
        return policyService.findByClientId(clientId);
    }
}
