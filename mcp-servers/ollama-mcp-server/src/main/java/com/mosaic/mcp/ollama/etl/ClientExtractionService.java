package com.mosaic.mcp.ollama.etl;

import com.mosaic.domain.client.Client;
import com.mosaic.domain.client.ClientService;
import com.mosaic.domain.client.ContactInfo;
import com.mosaic.domain.client.Identity;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Sprint 2's "real ETL workflow against digital input" (ARCHITECTURE.md
 * DoD #4): already-digital, agent-entered freeform intake text goes in,
 * Ollama extracts structured identity/contact fields, and a Client record
 * comes out. Linear and deterministic — no OCR, no confidence branching,
 * no human-in-the-loop review — matching Sprint 2's narrow ingestion scope.
 */
@Service
public class ClientExtractionService {

    private final ChatClient chatClient;
    private final ClientService clientService;

    public ClientExtractionService(ChatClient.Builder chatClientBuilder, ClientService clientService) {
        this.chatClient = chatClientBuilder.build();
        this.clientService = clientService;
    }

    public Client extractAndCreateClient(String rawIntakeText) {
        ExtractedClientInfo extracted = chatClient.prompt()
                .system("""
                        Extract the client's full name, date of birth, phone
                        number, and email address from the intake text.
                        Format dateOfBirth as yyyy-MM-dd. Respond with only
                        the requested fields.
                        """)
                .user(rawIntakeText)
                .call()
                .entity(ExtractedClientInfo.class);

        Client client = new Client();
        client.setIdentity(new Identity(extracted.fullName(), LocalDate.parse(extracted.dateOfBirth()), null));
        client.setContact(new ContactInfo(extracted.phone(), extracted.email()));
        return clientService.createClient(client);
    }
}
