package com.mosaic.api.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Throwaway consumer proving the ingestion-routing topic is wired
 * end-to-end (DoD #3). Not a real ETL consumer — that's the Ollama MCP's
 * job (DoD #4), once that module exists.
 */
@Component
public class IngestionRoutingConsumer {

    private static final Logger log = LoggerFactory.getLogger(IngestionRoutingConsumer.class);

    @KafkaListener(topics = KafkaTopics.INGESTION_ROUTING, groupId = "core-api-ingestion-routing-throwaway")
    public void onMessage(String message) {
        log.info("[ingestion-routing] received: {}", message);
    }
}
