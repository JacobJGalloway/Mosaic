package com.mosaic.mcp.internal;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(scanBasePackages = "com.mosaic")
@EnableMongoRepositories(basePackages = "com.mosaic.domain.client")
public class InternalMcpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(InternalMcpServerApplication.class, args);
    }

    @Bean
    public ToolCallbackProvider clientTools(ClientToolset clientToolset) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(clientToolset)
                .build();
    }
}
