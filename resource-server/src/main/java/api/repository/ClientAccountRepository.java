package api.repository;

import api.entity.ClientAccount;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientAccountRepository extends JpaRepository<ClientAccount, Long> {

    boolean existsByCpf (String cpf);

    Optional<ClientAccount> findByCpf (String cpf);

    Optional<ClientAccount> findByClientAuthUserId (Long authUserId);

    @Lock (value = LockModeType.PESSIMISTIC_WRITE)
    Optional<ClientAccount> findLockedById (Long id);
}
