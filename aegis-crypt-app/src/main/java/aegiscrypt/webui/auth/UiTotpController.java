package aegiscrypt.webui.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UiTotpController {
    @GetMapping("/ui/totp")
    public String totp() {
        return "totp";
    }
}
