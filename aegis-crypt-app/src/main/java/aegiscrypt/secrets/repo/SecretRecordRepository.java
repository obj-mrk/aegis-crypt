package aegiscrypt.secrets.repo;

import aegiscrypt.secrets.domain.SecretRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SecretRecordRepository extends JpaRepository<SecretRecordEntity, Long> {

    List<SecretRecordEntity> findByOwnerId(Long ownerId);
}
