package aegiscrypt.auth.web;

import aegiscrypt.auth.api.*;
import aegiscrypt.auth.service.TotpService;
import aegiscrypt.auth.service.TotpSetupService;
import aegiscrypt.user.repo.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/totp")
@RequiredArgsConstructor
public class PublicTotpSetupController {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final TotpSetupService totpSetupService;

    @PostMapping("/begin")
    public TotpSetupResponse begin(@Valid @RequestBody TotpSetupBeginRequest req) {
        var user = userRepo.findByEmail(req.email().toLowerCase()).orElseThrow(() -> new IllegalArgumentException("invalid credentials"));
        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) throw new IllegalArgumentException("invalid credentials");

        String secret = totpSetupService.begin(user.getId());
        String uri = TotpService.otpauthUri("aegis-crypt", user.getEmail(), secret, 6, 30);
        return new TotpSetupResponse(secret, uri);
    }

    @PostMapping("/confirm")
    public void confirm(@Valid @RequestBody TotpSetupConfirmRequest req) {
        var user = userRepo.findByEmail(req.email().toLowerCase()).orElseThrow(() -> new IllegalArgumentException("invalid credentials"));
        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) throw new IllegalArgumentException("invalid credentials");

        totpSetupService.confirm(user.getId(), req.totp());
    }
}
