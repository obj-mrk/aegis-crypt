package aegiscrypt.webui;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UiErrorController implements ErrorController {

    @RequestMapping("/error")
    public String error(HttpServletRequest req, Model model) {
        Object status = req.getAttribute("jakarta.servlet.error.status_code");
        Object message = req.getAttribute("jakarta.servlet.error.message");
        Object ex = req.getAttribute("jakarta.servlet.error.exception");

        model.addAttribute("status", status);
        model.addAttribute("message", message);
        model.addAttribute("exception", ex != null ? ex.toString() : null);

        return "error";
    }
}
