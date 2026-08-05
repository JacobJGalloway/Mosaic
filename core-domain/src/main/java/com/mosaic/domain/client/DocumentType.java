package com.mosaic.domain.client;

/**
 * IDENTITY_VERIFICATION and PROOF_OF_ADDRESS are required document
 * categories for client completeness, expected to grow (mortgage, deed,
 * trust-transfer, etc.) as new requirements are identified — see
 * OVERVIEW.md's Client Completeness section.
 * <p>
 * DATA_IMPORT marks an artifact that contributed data via direct field
 * mapping (e.g. a per-client row split out of a bulk CSV import) rather
 * than standing in for one of the required completeness documents above —
 * the completeness checker should skip it when scanning for required-document
 * slots, while the ETL adapter still uses it as the lineage record for
 * where the mapped fields came from.
 */
public enum DocumentType {
    IDENTITY_VERIFICATION,
    PROOF_OF_ADDRESS,
    DATA_IMPORT
}
