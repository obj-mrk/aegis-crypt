package aegiscrypt.webui.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UiRegisterSubmitController {

    private final RestClient client;

    public UiRegisterSubmitController(RestClient client) {
        this.client = client;
    }

    @PostMapping("/ui/register")
    public String register(@RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String displayName,
                           RedirectAttributes ra) {
        try {
            client.post().uri("/api/public/register")
                    .body(new RegisterDto(email, password, displayName))
                    .retrieve().toBodilessEntity();
            ra.addFlashAttribute("msg", "Registered. Now setup TOTP and login.");
            return "redirect:/ui/totp";
        } catch (Exception e) {
            ra.addFlashAttribute("err", "Register failed: " + e.getMessage());
            return "redirect:/ui/register";
        }
    }

    record RegisterDto(String email, String password, String displayName) {}
}
