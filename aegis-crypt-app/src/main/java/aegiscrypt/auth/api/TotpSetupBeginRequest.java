package aegiscrypt.auth.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record TotpSetupBeginRequest(
        @Email @NotBlank String email,
        @NotBlank String password
) {}
