package api.repository;

import api.entity.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface OutboxRepository extends JpaRepository<Outbox, Long> {

    @Query ("SELECT o FROM Outbox o WHERE o.isProcessed = false ORDER BY o.createdAt ASC")
    List<Outbox> findByIsProcessedFalseOrderedByCreatedAtAsc ();
}
