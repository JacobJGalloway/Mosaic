package com.mosaic.domain.client.completeness;

import com.mosaic.domain.client.Address;
import com.mosaic.domain.client.Client;
import com.mosaic.domain.client.ClientDocument;
import com.mosaic.domain.client.ContactInfo;
import com.mosaic.domain.client.DocumentType;
import com.mosaic.domain.client.Identity;
import com.mosaic.domain.client.PriorInsuranceRecord;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ClientCompletenessServiceTest {

    private final ClientCompletenessService service = new ClientCompletenessService();

    private Client completeClient() {
        Client client = new Client();
        client.setIdentity(new Identity("Jane Doe", LocalDate.of(1990, 1, 1),
                new Address("1 Main St", null, "Springfield", "IL", "62704", "USA")));
        client.setContact(new ContactInfo("555-1234", "jane@example.com"));
        client.setPriorInsurance(List.of(
                new PriorInsuranceRecord("Acme Insurance", "P123", LocalDate.of(2020, 1, 1), LocalDate.of(2021, 1, 1), true)));
        client.setDocuments(List.of(
                new ClientDocument(DocumentType.IDENTITY_VERIFICATION, "upload", null, "ref-1", null, null),
                new ClientDocument(DocumentType.PROOF_OF_ADDRESS, "upload", null, "ref-2", null, null)));
        return client;
    }

    @Test
    void fullyPopulatedClientIsComplete() {
        ClientCompletenessResult result = service.evaluate(completeClient());

        assertThat(result.complete()).isTrue();
        assertThat(result.missingRequirements()).isEmpty();
    }

    @Test
    void nullIdentityIsFlaggedMissing() {
        Client client = completeClient();
        client.setIdentity(null);

        ClientCompletenessResult result = service.evaluate(client);

        assertThat(result.complete()).isFalse();
        assertThat(result.missingRequirements()).containsExactly(CompletenessRequirement.IDENTITY);
    }

    @Test
    void identityWithoutAddressIsFlaggedMissing() {
        Client client = completeClient();
        client.setIdentity(new Identity("Jane Doe", LocalDate.of(1990, 1, 1), null));

        ClientCompletenessResult result = service.evaluate(client);

        assertThat(result.missingRequirements()).containsExactly(CompletenessRequirement.IDENTITY);
    }

    @Test
    void identityWithBlankFullNameIsFlaggedMissing() {
        Client client = completeClient();
        client.setIdentity(new Identity("   ", LocalDate.of(1990, 1, 1),
                new Address("1 Main St", null, "Springfield", "IL", "62704", "USA")));

        ClientCompletenessResult result = service.evaluate(client);

        assertThat(result.missingRequirements()).containsExactly(CompletenessRequirement.IDENTITY);
    }

    @Test
    void missingContactInfoIsFlagged() {
        Client client = completeClient();
        client.setContact(null);

        ClientCompletenessResult result = service.evaluate(client);

        assertThat(result.missingRequirements()).containsExactly(CompletenessRequirement.CONTACT_INFO);
    }

    @Test
    void emptyPriorInsuranceHistoryIsFlagged() {
        Client client = completeClient();
        client.setPriorInsurance(List.of());

        ClientCompletenessResult result = service.evaluate(client);

        assertThat(result.missingRequirements()).containsExactly(CompletenessRequirement.PRIOR_INSURANCE_HISTORY);
    }

    @Test
    void missingIdentityVerificationDocumentIsFlagged() {
        Client client = completeClient();
        client.setDocuments(List.of(
                new ClientDocument(DocumentType.PROOF_OF_ADDRESS, "upload", null, "ref-2", null, null)));

        ClientCompletenessResult result = service.evaluate(client);

        assertThat(result.missingRequirements()).containsExactly(CompletenessRequirement.IDENTITY_VERIFICATION_DOCUMENT);
    }

    @Test
    void missingProofOfAddressDocumentIsFlagged() {
        Client client = completeClient();
        client.setDocuments(List.of(
                new ClientDocument(DocumentType.IDENTITY_VERIFICATION, "upload", null, "ref-1", null, null)));

        ClientCompletenessResult result = service.evaluate(client);

        assertThat(result.missingRequirements()).containsExactly(CompletenessRequirement.PROOF_OF_ADDRESS_DOCUMENT);
    }

    @Test
    void emptyClientFlagsEveryApplicableRequirement() {
        Client client = new Client();

        ClientCompletenessResult result = service.evaluate(client);

        assertThat(result.complete()).isFalse();
        assertThat(result.missingRequirements()).containsExactlyInAnyOrder(
                CompletenessRequirement.IDENTITY,
                CompletenessRequirement.CONTACT_INFO,
                CompletenessRequirement.PRIOR_INSURANCE_HISTORY,
                CompletenessRequirement.IDENTITY_VERIFICATION_DOCUMENT,
                CompletenessRequirement.PROOF_OF_ADDRESS_DOCUMENT);
        // HOUSEHOLD_COMPOSITION is deliberately never flagged by this
        // rules-based check — see ClientCompletenessService's javadoc.
        assertThat(result.missingRequirements()).doesNotContain(CompletenessRequirement.HOUSEHOLD_COMPOSITION);
    }
}
