package com.mosaic.domain.client;

/**
 * Confidence-routing status for documents processed through the OCR/ETL
 * pipeline. A {@code ClientDocument} may have a null status when its data
 * did not arrive via that pipeline at all (e.g. manual or verbal capture).
 */
public enum DocumentStatus {
    PENDING,
    VERIFIED,
    NEEDS_REVIEW
}
