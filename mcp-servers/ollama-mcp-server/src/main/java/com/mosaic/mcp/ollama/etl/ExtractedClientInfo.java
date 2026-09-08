package com.mosaic.mcp.ollama.etl;

/**
 * Shape the Ollama model is asked to fill in from freeform intake text.
 * dateOfBirth is a plain yyyy-MM-dd string rather than a LocalDate so
 * parsing failures surface explicitly in ClientExtractionService instead
 * of inside Jackson's structured-output deserialization.
 */
public record ExtractedClientInfo(String fullName, String dateOfBirth, String phone, String email) {
}
