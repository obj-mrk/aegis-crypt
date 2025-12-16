package aegiscrypt.webui.secrets;

import aegiscrypt.webui.auth.UiAuthCookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestClient;

@Controller
public class UiSecretPdfProxyController {

    private final RestClient client;

    public UiSecretPdfProxyController(RestClient client) {
        this.client = client;
    }

    @GetMapping("/ui/user/secrets/{id}/pdf")
    public ResponseEntity<byte[]> pdf(@PathVariable Long id, HttpServletRequest req) {
        String jwt = UiAuthCookie.get(req);
        if (jwt == null) return ResponseEntity.status(302).header("Location", "/ui/login").build();

        byte[] pdf = client.get().uri("/api/user/secrets/{id}/pdf", id)
                .header("Authorization", "Bearer " + jwt)
                .retrieve()
                .body(byte[].class);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=secret-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
