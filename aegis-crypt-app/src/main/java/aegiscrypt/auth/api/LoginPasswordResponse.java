package aegiscrypt.auth.api;

import java.util.UUID;

public record LoginPasswordResponse(
        UUID authSessionId,
        UUID emailOtpId
) {}