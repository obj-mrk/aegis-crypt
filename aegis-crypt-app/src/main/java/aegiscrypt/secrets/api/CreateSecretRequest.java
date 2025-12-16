package aegiscrypt.secrets.api;

import aegiscrypt.secrets.domain.SecretAlgorithm;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateSecretRequest(
        @NotBlank String plaintext,
        @NotNull SecretAlgorithm algorithm
) {}
