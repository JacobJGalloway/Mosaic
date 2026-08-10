package com.mosaic.domain.client.completeness;

import com.mosaic.domain.client.Client;
import com.mosaic.domain.client.ClientDocument;
import com.mosaic.domain.client.DocumentType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Sprint 1 rules-based completeness check — a deliberately temporary,
 * non-AI gap check. Explicitly not extended in place; Sprint 4 replaces
 * this with true gap analysis via the Claude MCP + LangGraph4j orchestrator.
 * See ARCHITECTURE.md's Roadmap.
 */
@Service
public class ClientCompletenessService {

    public ClientCompletenessResult evaluate(Client client) {
        List<CompletenessRequirement> missing = new ArrayList<>();

        if (!hasIdentity(client)) {
            missing.add(CompletenessRequirement.IDENTITY);
        }
        if (!hasContactInfo(client)) {
            missing.add(CompletenessRequirement.CONTACT_INFO);
        }
        if (client.getPriorInsurance().isEmpty()) {
            missing.add(CompletenessRequirement.PRIOR_INSURANCE_HISTORY);
        }
        // HOUSEHOLD_COMPOSITION is "as applicable" per ARCHITECTURE.md — a
        // single-person household is legitimately empty. This rules-based
        // check can't distinguish "legitimately empty" from "not yet
        // collected," so it is never flagged missing here; true necessity
        // is a Sprint 2 gap-analysis concern.
        if (!hasDocument(client, DocumentType.IDENTITY_VERIFICATION)) {
            missing.add(CompletenessRequirement.IDENTITY_VERIFICATION_DOCUMENT);
        }
        if (!hasDocument(client, DocumentType.PROOF_OF_ADDRESS)) {
            missing.add(CompletenessRequirement.PROOF_OF_ADDRESS_DOCUMENT);
        }

        return new ClientCompletenessResult(missing.isEmpty(), missing);
    }

    private boolean hasIdentity(Client client) {
        var identity = client.getIdentity();
        return identity != null
                && identity.fullName() != null && !identity.fullName().isBlank()
                && identity.dateOfBirth() != null
                && identity.currentAddress() != null;
    }

    private boolean hasContactInfo(Client client) {
        var contact = client.getContact();
        return contact != null
                && contact.phone() != null && !contact.phone().isBlank()
                && contact.email() != null && !contact.email().isBlank();
    }

    private boolean hasDocument(Client client, DocumentType documentType) {
        return client.getDocuments().stream()
                .map(ClientDocument::documentType)
                .anyMatch(documentType::equals);
    }
}
