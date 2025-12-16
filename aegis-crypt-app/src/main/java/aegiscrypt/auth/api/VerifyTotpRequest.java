package aegiscrypt.auth.api;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record VerifyTotpRequest(
        UUID authSessionId,
        @NotBlank String totp
) {}