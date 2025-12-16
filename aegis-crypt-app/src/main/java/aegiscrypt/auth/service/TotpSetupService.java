package aegiscrypt.auth.service;

import aegiscrypt.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TotpSetupService {

    private final UserRepository userRepo;

    @Transactional
    public String begin(Long userId) {
        var user = userRepo.findById(userId).orElseThrow();
        String secret = TotpService.generateBase32Secret();
        user.setTotpSecret(secret);
        user.setTotpEnabled(false);
        return secret;
    }

    @Transactional
    public void confirm(Long userId, String totp) {
        var user = userRepo.findById(userId).orElseThrow();
        if (user.getTotpSecret() == null) throw new IllegalStateException("no totp secret");
        boolean ok = TotpService.verify(user.getTotpSecret(), totp, 30, 6, 1);
        if (!ok) throw new IllegalArgumentException("invalid totp");
        user.setTotpEnabled(true);
    }
}
