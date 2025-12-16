package aegiscrypt.webui.auth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;

@Controller
public class UiTotpSubmitController {

    private final RestClient client;

    public UiTotpSubmitController(RestClient client) {
        this.client = client;
    }

    @PostMapping("/ui/totp/begin")
    public String begin(@RequestParam String email,
                        @RequestParam String password,
                        Model model) {
        var resp = client.post().uri("/api/public/totp/begin")
                .body(new BeginDto(email, password))
                .retrieve()
                .body(TotpBeginResp.class);

        model.addAttribute("email", email);
        model.addAttribute("password", password);
        model.addAttribute("secret", resp.secret());
        model.addAttribute("otpauthUri", resp.otpauthUri());
        return "totp_begin_result";
    }

    @PostMapping("/ui/totp/confirm")
    public String confirm(@RequestParam String email,
                          @RequestParam String password,
                          @RequestParam String totp,
                          Model model) {
        client.post().uri("/api/public/totp/confirm")
                .body(new ConfirmDto(email, password, totp))
                .retrieve().toBodilessEntity();

        model.addAttribute("msg", "TOTP enabled. Proceed to login.");
        return "login";
    }

    record BeginDto(String email, String password) {}
    record ConfirmDto(String email, String password, String totp) {}
    record TotpBeginResp(String secret, String otpauthUri) {}
}
