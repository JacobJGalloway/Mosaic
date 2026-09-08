package com.mosaic.mcp.ollama.kafka;

/**
 * Mirrors core-api's com.mosaic.api.kafka.KafkaTopics. Duplicated rather
 * than shared because there's no shared module between core-api and the
 * MCP servers yet — worth factoring out if a third consumer of these
 * topic names shows up.
 */
public final class KafkaTopics {

    public static final String INGESTION_ROUTING = "ingestion-routing";
    public static final String MCP_LIFECYCLE_EVENTS = "mcp-lifecycle-events";

    private KafkaTopics() {
    }
}
