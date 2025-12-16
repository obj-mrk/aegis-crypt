package aegiscrypt.user.repo;

import aegiscrypt.user.domain.RoleCode;
import aegiscrypt.user.domain.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByCode(RoleCode code);
}
