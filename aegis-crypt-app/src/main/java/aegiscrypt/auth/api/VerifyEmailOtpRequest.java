package aegiscrypt.auth.api;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record VerifyEmailOtpRequest(
        UUID authSessionId,
        UUID emailOtpId,
        @NotBlank String code
) {}