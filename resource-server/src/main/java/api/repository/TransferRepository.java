package api.repository;

import api.entity.Transfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransferRepository extends JpaRepository<Transfer, Long> {

    @Query ("SELECT t FROM Transfer t WHERE t.sourceAccount.client.authUserId = :authUserId OR t.targetAccount.client.authUserId = :authUserId")
    Page<Transfer> findAllByAuthUserId (@Param (value = "authUserId") Long authUserId, Pageable pageable);
}
