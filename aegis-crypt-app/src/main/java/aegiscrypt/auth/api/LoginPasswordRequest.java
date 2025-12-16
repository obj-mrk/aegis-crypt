package aegiscrypt.auth.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginPasswordRequest(
        @Email @NotBlank String email,
        @NotBlank String password
) {}
