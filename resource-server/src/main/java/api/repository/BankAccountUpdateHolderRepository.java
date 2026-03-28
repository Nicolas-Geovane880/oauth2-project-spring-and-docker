package api.repository;

import api.entity.BankAccountUpdateHolder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BankAccountUpdateHolderRepository extends JpaRepository<BankAccountUpdateHolder, Long> {

    Optional<BankAccountUpdateHolder> findByClientCode (UUID clientCode);
}
