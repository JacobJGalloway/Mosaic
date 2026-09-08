package com.mosaic.mcp.ollama.etl;

import com.mosaic.domain.client.Client;
import com.mosaic.domain.client.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientExtractionServiceTest {

    @Mock
    private ChatClient.Builder chatClientBuilder;
    @Mock
    private ChatClient chatClient;
    @Mock
    private ChatClient.ChatClientRequestSpec requestSpec;
    @Mock
    private ChatClient.CallResponseSpec responseSpec;
    @Mock
    private ClientService clientService;

    private ClientExtractionService service;

    @BeforeEach
    void setUp() {
        when(chatClientBuilder.build()).thenReturn(chatClient);
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.system(anyString())).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);

        service = new ClientExtractionService(chatClientBuilder, clientService);
    }

    @Test
    void mapsExtractedFieldsIntoAPersistedClient() {
        ExtractedClientInfo extracted = new ExtractedClientInfo("Maria Gonzalez", "1985-03-14", "555-0100", "maria@example.com");
        when(responseSpec.entity(ExtractedClientInfo.class)).thenReturn(extracted);
        when(clientService.createClient(any(Client.class))).thenAnswer(inv -> inv.getArgument(0));

        Client client = service.extractAndCreateClient("freeform intake text");

        assertThat(client.getIdentity().fullName()).isEqualTo("Maria Gonzalez");
        assertThat(client.getIdentity().dateOfBirth()).isEqualTo(LocalDate.of(1985, 3, 14));
        assertThat(client.getContact().phone()).isEqualTo("555-0100");
        assertThat(client.getContact().email()).isEqualTo("maria@example.com");

        ArgumentCaptor<Client> captor = ArgumentCaptor.forClass(Client.class);
        verify(clientService).createClient(captor.capture());
        assertThat(captor.getValue()).isSameAs(client);
    }

    @Test
    void unparsableDateOfBirthFailsRatherThanSilentlyPersistingBadData() {
        ExtractedClientInfo extracted = new ExtractedClientInfo("Maria Gonzalez", "not-a-date", "555-0100", "maria@example.com");
        when(responseSpec.entity(ExtractedClientInfo.class)).thenReturn(extracted);

        assertThatThrownBy(() -> service.extractAndCreateClient("freeform intake text"))
                .isInstanceOf(java.time.format.DateTimeParseException.class);
    }
}
