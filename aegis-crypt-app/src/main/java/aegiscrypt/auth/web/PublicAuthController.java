package aegiscrypt.auth.web;

import aegiscrypt.auth.api.*;
import aegiscrypt.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicAuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        authService.register(req.email(), req.password(), req.displayName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login/password")
    public LoginPasswordResponse loginPassword(@Valid @RequestBody LoginPasswordRequest req) {
        var r = authService.loginPassword(req.email(), req.password());
        return new LoginPasswordResponse(r.authSessionId(), r.emailOtpId());
    }
}
