package api.repository;

import api.entity.ClientAccount;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientAccountRepository extends JpaRepository<ClientAccount, Long> {

    boolean existsByCpf (String cpf);

    Optional<ClientAccount> findByCpf (String cpf);

    @Lock (value = LockModeType.PESSIMISTIC_WRITE)
    Optional<ClientAccount> findLockedById (Long id);

    void deleteByClientCode(UUID code);

    Optional<ClientAccount> findByClientCode (UUID clientCode);
}
