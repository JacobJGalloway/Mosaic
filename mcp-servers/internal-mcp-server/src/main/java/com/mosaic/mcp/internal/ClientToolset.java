package com.mosaic.mcp.internal;

import com.mosaic.domain.client.Client;
import com.mosaic.domain.client.ClientService;
import com.mosaic.domain.client.completeness.ClientCompletenessResult;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class ClientToolset {

    private final ClientService clientService;

    public ClientToolset(ClientService clientService) {
        this.clientService = clientService;
    }

    @Tool(description = "Create a new client record from a composite client object")
    public Client createClient(@ToolParam(description = "The client to create") Client client) {
        return clientService.createClient(client);
    }

    @Tool(description = "Fetch a client record by its id")
    public Client fetchClient(@ToolParam(description = "The client id") String clientId) {
        return clientService.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("No client found with id " + clientId));
    }

    @Tool(description = "Check whether a client record has all Sprint 1 required fields and documents")
    public ClientCompletenessResult checkClientCompleteness(@ToolParam(description = "The client id") String clientId) {
        return clientService.checkCompleteness(clientId);
    }
}
