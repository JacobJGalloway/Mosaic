package com.mosaic.api.kafka;

/**
 * Topic names, mirrored by docker-compose.yml's kafka-topic-init service.
 * Sprint 2 sizes both as single-partition — see ARCHITECTURE.md's Open
 * Implementation Questions on partitioning/consumer-group sizing.
 */
public final class KafkaTopics {

    public static final String INGESTION_ROUTING = "ingestion-routing";
    public static final String MCP_LIFECYCLE_EVENTS = "mcp-lifecycle-events";

    private KafkaTopics() {
    }
}
