package aegiscrypt.auth.api;

import jakarta.validation.constraints.NotBlank;

public record TotpConfirmRequest(@NotBlank String totp) {
}
