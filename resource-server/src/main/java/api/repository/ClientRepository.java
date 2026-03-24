package api.repository;

import api.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, Long> {

    boolean existsByEmail (String email);

    boolean existsByPhone (String phone);

    boolean existsByCode (UUID code);

    void deleteByCode (UUID code);

    Optional<Client> findByCode(UUID code);
}
