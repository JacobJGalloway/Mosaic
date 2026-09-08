package com.mosaic.api.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Throwaway consumer proving the mcp-lifecycle-events topic is wired
 * end-to-end (DoD #3). Real MCP-server self-registration (register/
 * heartbeat/shutdown) is produced starting with the Ollama MCP (DoD #4).
 */
@Component
public class McpLifecycleEventsConsumer {

    private static final Logger log = LoggerFactory.getLogger(McpLifecycleEventsConsumer.class);

    @KafkaListener(topics = KafkaTopics.MCP_LIFECYCLE_EVENTS, groupId = "core-api-mcp-lifecycle-throwaway")
    public void onMessage(String message) {
        log.info("[mcp-lifecycle-events] received: {}", message);
    }
}
