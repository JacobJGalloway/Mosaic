package com.mosaic.mcp.ollama.etl;

import com.mosaic.domain.client.Client;
import com.mosaic.mcp.ollama.kafka.KafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class IngestionRoutingEtlConsumer {

    private static final Logger log = LoggerFactory.getLogger(IngestionRoutingEtlConsumer.class);

    private final ClientExtractionService clientExtractionService;

    public IngestionRoutingEtlConsumer(ClientExtractionService clientExtractionService) {
        this.clientExtractionService = clientExtractionService;
    }

    @KafkaListener(topics = KafkaTopics.INGESTION_ROUTING, groupId = "ollama-mcp-server-etl")
    public void onMessage(String rawIntakeText) {
        Client client = clientExtractionService.extractAndCreateClient(rawIntakeText);
        log.info("[ingestion-routing] ETL created client id={}", client.getId());
    }
}
