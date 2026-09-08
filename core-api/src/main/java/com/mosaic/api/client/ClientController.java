package com.mosaic.api.client;

import com.mosaic.domain.auth.ActionIds;
import com.mosaic.domain.client.Client;
import com.mosaic.domain.client.ClientService;
import com.mosaic.domain.client.completeness.ClientCompletenessResult;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('" + ActionIds.CLIENT_CREATE + "')")
    public ResponseEntity<Client> createClient(@Valid @RequestBody Client client) {
        Client created = clientService.createClient(client);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('" + ActionIds.CLIENT_READ + "')")
    public Client fetchClient(@PathVariable String id) {
        return clientService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No client found with id " + id));
    }

    @GetMapping("/{id}/completeness")
    @PreAuthorize("hasAuthority('" + ActionIds.CLIENT_READ + "')")
    public ClientCompletenessResult checkCompleteness(@PathVariable String id) {
        try {
            return clientService.checkCompleteness(id);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
