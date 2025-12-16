package aegiscrypt.auth.repo;

import aegiscrypt.auth.domain.EmailOtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public interface EmailOtpRepository extends JpaRepository<EmailOtpEntity, UUID> {
    Optional<EmailOtpEntity> findByIdAndExpiresAtAfter(UUID id, OffsetDateTime now);
}
