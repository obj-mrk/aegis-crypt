package aegiscrypt.pdf;

import aegiscrypt.secrets.repo.SecretRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/secrets")
@RequiredArgsConstructor
public class UserPdfController {

    private final SecretRecordRepository repo;
    private final PdfExportService pdf;

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> export(@PathVariable Long id, Authentication auth) {
        Long userId = Long.valueOf(auth.getName());

        var secret = repo.findById(id).orElseThrow();
        if (!secret.getOwner().getId().equals(userId)) {
            // лучше 403 чем "не найдено", чтобы отладка была проще в учебном проекте
            return ResponseEntity.status(403).build();
        }

        byte[] bytes = pdf.exportSecret(secret);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=secret-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(bytes);
    }
}
