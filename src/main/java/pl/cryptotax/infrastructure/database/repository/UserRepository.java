package pl.cryptotax.infrastructure.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cryptotax.infrastructure.database.entity.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
