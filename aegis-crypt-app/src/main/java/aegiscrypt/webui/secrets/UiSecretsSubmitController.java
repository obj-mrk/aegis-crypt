package aegiscrypt.webui.secrets;

import aegiscrypt.webui.auth.UiAuthCookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UiSecretsSubmitController {

    private final RestClient client;

    public UiSecretsSubmitController(RestClient client) {
        this.client = client;
    }

    @PostMapping("/ui/user/secrets")
    public String create(HttpServletRequest req,
                         @RequestParam String plaintext,
                         @RequestParam String algorithm,
                         RedirectAttributes ra) {
        String jwt = UiAuthCookie.get(req);
        if (jwt == null) return "redirect:/ui/login";

        try {
            client.post().uri("/api/user/secrets")
                    .header("Authorization", "Bearer " + jwt)
                    .body(new CreateDto(plaintext, algorithm))
                    .retrieve().toBodilessEntity();
            ra.addFlashAttribute("msg", "Secret created");
        } catch (Exception e) {
            ra.addFlashAttribute("err", "Create failed: " + e.getMessage());
        }

        return "redirect:/ui/user/secrets";
    }

    record CreateDto(String plaintext, String algorithm) {}
}
