package aegiscrypt.webui.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UiLoginController {

    @GetMapping("/ui/login")
    public String login() {
        return "login";
    }
}
