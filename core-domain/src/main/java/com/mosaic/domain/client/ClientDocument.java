package com.mosaic.domain.client;

import java.time.Instant;

/**
 * {@code sourceType} is a registry-validated key (not a compiled enum) so
 * new intake sources can be added without a rebuild/redeploy. {@code status}
 * is nullable — it only applies to documents routed through the OCR/ETL
 * confidence pipeline.
 */
public record ClientDocument(
        DocumentType documentType,
        String sourceType,
        DocumentStatus status,
        String storageReference,
        Double ocrConfidenceScore,
        Instant capturedAt
) {
}
