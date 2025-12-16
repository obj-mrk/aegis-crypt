package aegiscrypt.auth.service;

import aegiscrypt.auth.domain.*;
import aegiscrypt.auth.repo.AuthSessionRepository;
import aegiscrypt.auth.repo.EmailOtpRepository;
import aegiscrypt.security.jwt.JwtService;
import aegiscrypt.auth.domain.AuthStage;
import aegiscrypt.user.domain.RoleCode;
import aegiscrypt.user.domain.UserEntity;
import aegiscrypt.user.repo.RoleRepository;
import aegiscrypt.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;

    private final AuthSessionRepository sessionRepo;
    private final EmailOtpRepository otpRepo;

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final OtpSender otpSender;

    private static final SecureRandom RND = new SecureRandom();

    private final int otpTtlSeconds = 180;
    private final int sessionTtlSeconds = 300;

    @Transactional
    public void register(String email, String rawPassword, String displayName) {
        if (userRepo.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("email already registered");
        }

        var user = new UserEntity();
        user.setEmail(email.toLowerCase());
        user.setDisplayName(displayName);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setBlocked(false);
        user.setTotpEnabled(false);
        user.setTotpSecret(null);

        var userRole = roleRepo.findByCode(RoleCode.USER)
                .orElseThrow(() -> new IllegalStateException("Role USER not found in DB"));
        user.getRoles().add(userRole);

        userRepo.save(user);
    }

    @Transactional
    public LoginResult loginPassword(String email, String rawPassword) {
        UserEntity user = userRepo.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("invalid credentials"));
        if (user.isBlocked()) {
            throw new IllegalStateException("user blocked");
        }
        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("invalid credentials");
        }

        // auth session
        AuthSessionEntity session = new AuthSessionEntity();
        session.setId(UUID.randomUUID());
        session.setUser(user);
        session.setStage(AuthStage.PASSWORD_OK);
        session.setExpiresAt(OffsetDateTime.now().plusSeconds(sessionTtlSeconds));
        sessionRepo.save(session);

        // email otp
        String code = String.format("%06d", RND.nextInt(1_000_000));

        EmailOtpEntity otp = new EmailOtpEntity();
        otp.setId(UUID.randomUUID());
        otp.setUser(user);
        otp.setCodeHash(passwordEncoder.encode(code)); // BCrypt hash for otp too
        otp.setAttempts(0);
        otp.setMaxAttempts(5);
        otp.setConsumed(false);
        otp.setExpiresAt(OffsetDateTime.now().plusSeconds(otpTtlSeconds));
        otpRepo.save(otp);

        otpSender.send(user.getEmail(), code);

        return new LoginResult(session.getId(), otp.getId());
    }

    @Transactional
    public void verifyEmailOtp(UUID authSessionId, UUID emailOtpId, String code) {
        AuthSessionEntity session = sessionRepo.findByIdAndExpiresAtAfter(authSessionId, OffsetDateTime.now())
                .orElseThrow(() -> new IllegalArgumentException("invalid session"));
        if (session.getStage() != AuthStage.PASSWORD_OK) {
            throw new IllegalStateException("invalid stage: " + session.getStage());
        }

        EmailOtpEntity otp = otpRepo.findByIdAndExpiresAtAfter(emailOtpId, OffsetDateTime.now())
                .orElseThrow(() -> new IllegalArgumentException("invalid otp"));
        if (otp.isConsumed()) {
            throw new IllegalStateException("otp already used");
        }
        if (!otp.getUser().getId().equals(session.getUser().getId())) {
            throw new IllegalArgumentException("otp not for this session user");
        }

        if (otp.getAttempts() >= otp.getMaxAttempts()) {
            throw new IllegalStateException("otp attempts exceeded");
        }

        otp.setAttempts(otp.getAttempts() + 1);

        if (!passwordEncoder.matches(code, otp.getCodeHash())) {
            throw new IllegalArgumentException("invalid otp code");
        }

        otp.setConsumed(true);
        session.setStage(AuthStage.EMAIL_OTP_OK);
    }

    @Transactional
    public String verifyTotpAndIssueJwt(UUID authSessionId, String totp) {
        AuthSessionEntity session = sessionRepo.findByIdAndExpiresAtAfter(authSessionId, OffsetDateTime.now())
                .orElseThrow(() -> new IllegalArgumentException("invalid session"));

        if (session.getStage() != AuthStage.EMAIL_OTP_OK) {
            throw new IllegalStateException("invalid stage: " + session.getStage());
        }

        UserEntity user = session.getUser();
        if (!user.isTotpEnabled() || user.getTotpSecret() == null) {
            throw new IllegalStateException("totp is not enabled for user");
        }

        boolean ok = TotpService.verify(user.getTotpSecret(), totp, 30, 6, 1);
        if (!ok) {
            throw new IllegalArgumentException("invalid totp");
        }

        session.setStage(AuthStage.TOTP_OK);

        Set<RoleCode> roles = user.getRoles().stream().map(r -> r.getCode()).collect(java.util.stream.Collectors.toSet());
        return jwtService.issue(user.getId(), user.getEmail(), roles);
    }

    public record LoginResult(UUID authSessionId, UUID emailOtpId) {}
}
