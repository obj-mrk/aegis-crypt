package aegiscrypt.webui.auth;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Controller
public class UiLoginSubmitController {

    private final RestClient client;

    public UiLoginSubmitController(RestClient client) {
        this.client = client;
    }

    @PostMapping("/ui/login/password")
    public String step1(@RequestParam String email,
                        @RequestParam String password,
                        Model model) {
        var resp = client.post().uri("/api/public/login/password")
                .body(new LoginDto(email, password))
                .retrieve()
                .body(LoginResp.class);

        model.addAttribute("email", email);
        model.addAttribute("password", password);
        model.addAttribute("authSessionId", resp.authSessionId());
        model.addAttribute("emailOtpId", resp.emailOtpId());
        return "login_step2";
    }

    @PostMapping("/ui/login/email-otp")
    public String step2(@RequestParam UUID authSessionId,
                        @RequestParam UUID emailOtpId,
                        @RequestParam String code,
                        Model model) {
        client.post().uri("/api/auth/login/email-otp")
                .body(new OtpVerifyDto(authSessionId, emailOtpId, code))
                .retrieve().toBodilessEntity();

        model.addAttribute("authSessionId", authSessionId);
        return "login_step3";
    }

    @PostMapping("/ui/login/totp")
    public String step3(@RequestParam UUID authSessionId,
                        @RequestParam String totp,
                        HttpServletResponse response) {
        var jwt = client.post().uri("/api/auth/login/totp")
                .body(new TotpVerifyDto(authSessionId, totp))
                .retrieve()
                .body(JwtResp.class);

        UiAuthCookie.set(response, jwt.token());
        return "redirect:/ui/user/secrets";
    }

    @PostMapping("/ui/logout")
    public String logout(HttpServletResponse resp) {
        UiAuthCookie.clear(resp);
        return "redirect:/";
    }

    record LoginDto(String email, String password) {}
    record LoginResp(UUID authSessionId, UUID emailOtpId) {}

    record OtpVerifyDto(UUID authSessionId, UUID emailOtpId, String code) {}
    record TotpVerifyDto(UUID authSessionId, String totp) {}
    record JwtResp(String token) {}
}
