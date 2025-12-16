package aegiscrypt.auth.domain;

import aegiscrypt.user.domain.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "email_otp")
@Getter @Setter
public class EmailOtpEntity {

    @Id
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name="user_id", nullable = false)
    private UserEntity user;

    @Column(name="code_hash", nullable = false)
    private String codeHash;

    @Column(nullable = false)
    private int attempts;

    @Column(name="max_attempts", nullable = false)
    private int maxAttempts;

    @Column(nullable = false)
    private boolean consumed;

    @Column(name="expires_at", nullable = false)
    private OffsetDateTime expiresAt;

    @Column(name="created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void prePersist() {
        createdAt = OffsetDateTime.now();
    }
}
