package api.repository;

import api.entity.AccountTransferLock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountTransferLockRepository extends JpaRepository<AccountTransferLock, Long> {



}
