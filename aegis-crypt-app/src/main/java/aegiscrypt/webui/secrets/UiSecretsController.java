package aegiscrypt.webui.secrets;

import aegiscrypt.webui.auth.UiAuthCookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestClient;

import java.util.List;

@Controller
public class UiSecretsController {

    private final RestClient client;

    public UiSecretsController(RestClient client) {
        this.client = client;
    }

    @GetMapping("/ui/user/secrets")
    public String secrets(HttpServletRequest req, Model model) {
        String jwt = UiAuthCookie.get(req);
        if (jwt == null) return "redirect:/ui/login";

        List<SecretDto> secrets = client.get().uri("/api/user/secrets")
                .header("Authorization", "Bearer " + jwt)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        model.addAttribute("secrets", secrets);
        return "secrets";
    }

    public record SecretDto(Long id, String plaintext, String ciphertextBase64, String algorithm, String createdAt) {}
}
