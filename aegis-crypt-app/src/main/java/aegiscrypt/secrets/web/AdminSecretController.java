package aegiscrypt.secrets.web;

import aegiscrypt.secrets.api.DecryptSecretResponse;
import aegiscrypt.secrets.api.SecretResponse;
import aegiscrypt.secrets.service.SecretService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/secrets")
@RequiredArgsConstructor
public class AdminSecretController {

    private final SecretService service;

    @GetMapping
    public List<SecretResponse> listAll() {
        return service.listAll();
    }

    @GetMapping("/{id}/decrypt")
    public DecryptSecretResponse decrypt(@PathVariable Long id) {
        return service.decryptForAdmin(id);
    }
}
