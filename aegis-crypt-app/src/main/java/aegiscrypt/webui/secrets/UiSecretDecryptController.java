package aegiscrypt.webui.secrets;

import aegiscrypt.webui.auth.UiAuthCookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestClient;

@Controller
public class UiSecretDecryptController {

    private final RestClient client;

    public UiSecretDecryptController(RestClient client) {
        this.client = client;
    }

    @GetMapping("/ui/user/secrets/{id}/decrypt")
    public String decrypt(@PathVariable Long id, HttpServletRequest req, Model model) {
        String jwt = UiAuthCookie.get(req);
        if (jwt == null) return "redirect:/ui/login";

        var resp = client.get().uri("/api/user/secrets/{id}/decrypt", id)
                .header("Authorization", "Bearer " + jwt)
                .retrieve()
                .body(DecryptResp.class);

        model.addAttribute("id", resp.id());
        model.addAttribute("algorithm", resp.algorithm());
        model.addAttribute("plaintext", resp.plaintext());
        return "decrypt";
    }

    record DecryptResp(Long id, String algorithm, String plaintext) {}
}
