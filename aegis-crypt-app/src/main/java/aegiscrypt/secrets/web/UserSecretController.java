package aegiscrypt.secrets.web;

import aegiscrypt.secrets.api.CreateSecretRequest;
import aegiscrypt.secrets.api.DecryptSecretResponse;
import aegiscrypt.secrets.api.SecretResponse;
import aegiscrypt.secrets.service.SecretService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/secrets")
@RequiredArgsConstructor
public class UserSecretController {

    private final SecretService service;

    @PostMapping
    public SecretResponse create(
            Authentication auth,
            @Valid @RequestBody CreateSecretRequest req
    ) {
        Long userId = Long.valueOf(auth.getName());
        return service.create(userId, req.plaintext(), req.algorithm());
    }

    @GetMapping
    public List<SecretResponse> list(Authentication auth) {
        Long userId = Long.valueOf(auth.getName());
        return service.listForUser(userId);
    }

    @GetMapping("/{id}/decrypt")
    public DecryptSecretResponse decrypt(@PathVariable Long id, Authentication auth) {
        Long userId = Long.valueOf(auth.getName());
        return service.decryptForUser(userId, id);
    }
}
