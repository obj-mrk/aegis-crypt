package aegiscrypt.auth.service;

public interface OtpSender {
    void send(String email, String code);
}
