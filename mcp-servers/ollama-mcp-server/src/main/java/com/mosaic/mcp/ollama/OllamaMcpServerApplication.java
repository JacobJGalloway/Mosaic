package com.mosaic.mcp.ollama;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * No MCP tools are registered yet — the extraction workflow runs only via
 * the Kafka-triggered path (IngestionRoutingEtlConsumer). Exposing it as
 * an on-demand MCP tool too would create a circular dependency: Spring
 * AI's ChatClient.Builder auto-config wires its toolCallbackResolver to
 * enumerate every ToolCallbackProvider bean in the context as a tool the
 * model itself can call, and ClientExtractionService (the tool's own
 * implementation) needs that same ChatClient.Builder. Revisit once
 * there's a clean way to exclude a tool from the model's own
 * function-calling set.
 */
@SpringBootApplication(scanBasePackages = "com.mosaic")
@EnableMongoRepositories(basePackages = "com.mosaic.domain")
@EnableScheduling
public class OllamaMcpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OllamaMcpServerApplication.class, args);
    }
}
