package aegiscrypt.secrets.api;

import aegiscrypt.secrets.domain.SecretAlgorithm;

public record DecryptSecretResponse(
        Long id,
        SecretAlgorithm algorithm,
        String plaintext
) {}
