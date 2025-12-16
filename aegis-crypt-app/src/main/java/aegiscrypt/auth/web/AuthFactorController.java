package aegiscrypt.auth.web;

import aegiscrypt.auth.api.*;
import aegiscrypt.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthFactorController {

    private final AuthService authService;

    @PostMapping("/login/email-otp")
    public void verifyEmailOtp(@Valid @RequestBody VerifyEmailOtpRequest req) {
        authService.verifyEmailOtp(req.authSessionId(), req.emailOtpId(), req.code());
    }

    @PostMapping("/login/totp")
    public JwtResponse verifyTotp(@Valid @RequestBody VerifyTotpRequest req) {
        String jwt = authService.verifyTotpAndIssueJwt(req.authSessionId(), req.totp());
        return new JwtResponse(jwt);
    }
}
