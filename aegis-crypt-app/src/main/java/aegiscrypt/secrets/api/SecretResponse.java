package aegiscrypt.secrets.api;

import aegiscrypt.secrets.domain.SecretAlgorithm;

import java.time.OffsetDateTime;

public record SecretResponse(
        Long id,
        String plaintext,
        String ciphertextBase64,
        SecretAlgorithm algorithm,
        OffsetDateTime createdAt
) {}
