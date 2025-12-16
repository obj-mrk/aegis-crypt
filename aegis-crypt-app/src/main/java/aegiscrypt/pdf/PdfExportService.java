package aegiscrypt.pdf;

import aegiscrypt.secrets.domain.SecretRecordEntity;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class PdfExportService {

    private static final DateTimeFormatter TS = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public byte[] exportSecret(SecretRecordEntity s) {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                float margin = 48;
                float y = page.getMediaBox().getHeight() - margin;

                // Title
                y = writeLine(cs, PDType1Font.HELVETICA_BOLD, 16, margin, y, "Aegis-Crypt Secret Export");
                y -= 10;

                // Fields
                List<String> lines = new ArrayList<>();
                lines.add("Secret ID: " + s.getId());
                lines.add("Owner ID: " + (s.getOwner() != null ? s.getOwner().getId() : "N/A"));
                lines.add("Algorithm: " + s.getAlgorithm());
                lines.add("Created At: " + (s.getCreatedAt() != null ? TS.format(s.getCreatedAt()) : "N/A"));
                lines.add("");

                // plaintext
                lines.add("Plaintext:");
                lines.add(s.getPlaintext() != null ? s.getPlaintext() : "<not stored>");
                lines.add("");

                // ciphertext
                lines.add("Ciphertext (Base64):");
                lines.add(s.getCiphertextBase64());
                lines.add("");

                // meta (ограничим, чтобы не раздувать PDF)
                lines.add("Meta (JSON, truncated):");
                lines.add(truncate(s.getMetaJson(), 800));

                y = writeBlock(cs, margin, y, lines);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("PDF export failed", e);
        }
    }

    private static float writeLine(PDPageContentStream cs,
                                   org.apache.pdfbox.pdmodel.font.PDFont font,
                                   float fontSize,
                                   float x,
                                   float y,
                                   String text) throws Exception {
        cs.beginText();
        cs.setFont(font, fontSize);
        cs.newLineAtOffset(x, y);
        cs.showText(safe(text));
        cs.endText();
        return y - (fontSize + 4);
    }

    private static float writeBlock(PDPageContentStream cs, float x, float y, List<String> lines) throws Exception {
        cs.setFont(PDType1Font.HELVETICA, 11);

        float leading = 14;
        float maxY = 48; // bottom margin
        float maxWidth = PDRectangle.A4.getWidth() - 2 * x;

        for (String raw : lines) {
            for (String line : wrap(raw, 90)) { // простой wrap по символам (для Base64 хватает)
                if (y < maxY) {
                    // В Iteration 3 оставим 1 страницу. Если надо — сделаем многопоточность страниц.
                    // Но для лабы обычно влезает.
                    line = line.substring(0, Math.min(line.length(), 90));
                }
                cs.beginText();
                cs.newLineAtOffset(x, y);
                cs.showText(safe(line));
                cs.endText();
                y -= leading;
                if (y < maxY) break;
            }
            if (y < maxY) break;
        }
        return y;
    }

    private static List<String> wrap(String s, int maxLen) {
        List<String> out = new ArrayList<>();
        if (s == null) {
            out.add("");
            return out;
        }
        if (s.length() <= maxLen) {
            out.add(s);
            return out;
        }
        int i = 0;
        while (i < s.length()) {
            int j = Math.min(i + maxLen, s.length());
            out.add(s.substring(i, j));
            i = j;
        }
        return out;
    }

    private static String truncate(String s, int max) {
        if (s == null) return "<empty>";
        if (s.length() <= max) return s;
        return s.substring(0, max) + "...";
    }

    private static String safe(String s) {
        // PDFBox showText не любит некоторые управляющие символы.
        return (s == null ? "" : s.replace("\r", "").replace("\n", " "));
    }
}
