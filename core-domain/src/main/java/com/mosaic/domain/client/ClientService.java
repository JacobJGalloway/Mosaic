package com.mosaic.domain.client;

import com.mosaic.domain.client.completeness.ClientCompletenessResult;
import com.mosaic.domain.client.completeness.ClientCompletenessService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientCompletenessService completenessService;

    public ClientService(ClientRepository clientRepository, ClientCompletenessService completenessService) {
        this.clientRepository = clientRepository;
        this.completenessService = completenessService;
    }

    public Client createClient(Client client) {
        Instant now = Instant.now();
        client.setCreatedAt(now);
        client.setUpdatedAt(now);
        return clientRepository.save(client);
    }

    public Optional<Client> findById(String id) {
        return clientRepository.findById(id);
    }

    public ClientCompletenessResult checkCompleteness(String id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No client found with id " + id));
        return completenessService.evaluate(client);
    }
}
