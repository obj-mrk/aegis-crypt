package aegiscrypt.auth.api;

public record TotpSetupResponse(String secret, String otpauthUri) {}
