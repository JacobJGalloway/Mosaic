package com.mosaic.mcp.ollama.kafka;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Publishes register/heartbeat/shutdown lifecycle events to the
 * mcp-lifecycle-events topic — ARCHITECTURE.md DoD #4's self-registration
 * requirement.
 */
@Component
public class LifecycleEventPublisher {

    private static final String SERVER_NAME = "ollama-mcp-server";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public LifecycleEventPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        publish("register");
    }

    @Scheduled(fixedRateString = "${mosaic.mcp.heartbeat-interval-ms:30000}")
    public void onHeartbeat() {
        publish("heartbeat");
    }

    @EventListener(ContextClosedEvent.class)
    public void onShutdown() {
        publish("shutdown");
        // Context is closing right after this listener returns — flush so
        // the shutdown event actually reaches the broker before the async
        // producer gets torn down.
        kafkaTemplate.flush();
    }

    private void publish(String event) {
        String payload = "{\"server\":\"%s\",\"event\":\"%s\",\"timestamp\":\"%s\"}"
                .formatted(SERVER_NAME, event, Instant.now());
        kafkaTemplate.send(KafkaTopics.MCP_LIFECYCLE_EVENTS, payload);
    }
}
