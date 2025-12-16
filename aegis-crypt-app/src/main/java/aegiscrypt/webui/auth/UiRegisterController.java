package aegiscrypt.webui.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UiRegisterController {

    @GetMapping("/ui/register")
    public String register() {
        return "register";
    }
}
