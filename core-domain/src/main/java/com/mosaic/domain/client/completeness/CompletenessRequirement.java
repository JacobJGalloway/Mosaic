package com.mosaic.domain.client.completeness;

/**
 * Client-level completeness categories — see ARCHITECTURE.md's
 * Client Completeness Schema. Policy-specific (home/auto) requirements
 * are a Sprint 2 concern layered on top of this once a client is complete.
 */
public enum CompletenessRequirement {
    IDENTITY,
    CONTACT_INFO,
    PRIOR_INSURANCE_HISTORY,
    HOUSEHOLD_COMPOSITION,
    IDENTITY_VERIFICATION_DOCUMENT,
    PROOF_OF_ADDRESS_DOCUMENT
}
