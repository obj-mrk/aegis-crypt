package aegiscrypt.auth.repo;

import aegiscrypt.auth.domain.AuthSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public interface AuthSessionRepository extends JpaRepository<AuthSessionEntity, UUID> {
    Optional<AuthSessionEntity> findByIdAndExpiresAtAfter(UUID id, OffsetDateTime now);
}
