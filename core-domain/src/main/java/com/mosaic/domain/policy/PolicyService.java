package com.mosaic.domain.policy;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class PolicyService {

    private final PolicyRepository policyRepository;

    public PolicyService(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    public Policy createPolicy(Policy policy) {
        Instant now = Instant.now();
        policy.setCreatedAt(now);
        policy.setUpdatedAt(now);
        return policyRepository.save(policy);
    }

    public Optional<Policy> findById(String id) {
        return policyRepository.findById(id);
    }

    public List<Policy> findByClientId(String clientId) {
        return policyRepository.findByClientId(clientId);
    }
}
