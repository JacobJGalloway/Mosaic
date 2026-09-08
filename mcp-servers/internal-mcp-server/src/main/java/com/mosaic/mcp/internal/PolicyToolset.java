package com.mosaic.mcp.internal;

import com.mosaic.domain.policy.Policy;
import com.mosaic.domain.policy.PolicyService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PolicyToolset {

    private final PolicyService policyService;

    public PolicyToolset(PolicyService policyService) {
        this.policyService = policyService;
    }

    @Tool(description = "Create a new home or auto policy record, associated to an existing client by clientId")
    public Policy createPolicy(@ToolParam(description = "The policy to create") Policy policy) {
        return policyService.createPolicy(policy);
    }

    @Tool(description = "Fetch a policy record by its id")
    public Policy fetchPolicy(@ToolParam(description = "The policy id") String policyId) {
        return policyService.findById(policyId)
                .orElseThrow(() -> new IllegalArgumentException("No policy found with id " + policyId));
    }

    @Tool(description = "Fetch all policy records associated with a given client")
    public List<Policy> fetchPoliciesForClient(@ToolParam(description = "The client id") String clientId) {
        return policyService.findByClientId(clientId);
    }
}
