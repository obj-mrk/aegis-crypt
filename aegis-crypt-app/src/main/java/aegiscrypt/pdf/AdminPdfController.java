package aegiscrypt.pdf;

import aegiscrypt.secrets.repo.SecretRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/secrets")
@RequiredArgsConstructor
public class AdminPdfController {

    private final SecretRecordRepository repo;
    private final PdfExportService pdf;

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> export(@PathVariable Long id) {
        var secret = repo.findById(id).orElseThrow();
        byte[] bytes = pdf.exportSecret(secret);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=secret-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(bytes);
    }
}
