package aegiscrypt.auth.service.impl;

import aegiscrypt.auth.service.OtpSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Primary
@Component
@RequiredArgsConstructor
public class MailOtpSender implements OtpSender {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:no-reply@aegis-crypt.local}")
    private String from;

    @Override
    public void send(String email, String code) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(from);
            msg.setTo(email);
            msg.setSubject("Aegis-Crypt OTP Code");
            msg.setText("""
                    Your one-time code: %s
                    
                    If you did not request it, ignore this email.
                    """.formatted(code));

            mailSender.send(msg);
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}", email, e);
            throw e;
        }
    }
}
