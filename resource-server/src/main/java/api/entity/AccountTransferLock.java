package api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table (name = "account_transfer_lock")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountTransferLock {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @Column (name = "is_locked", nullable = false)
    private boolean isLocked = false;

    @Column (name = "locked_at")
    private LocalDate lockedAt;

    @Builder.Default
    @Column (name = "total_value_transferred_today", nullable = false)
    private Double totalValueTransferredToday = 0.0;

    //I want to limit the transferred values per day
    //LocalDate works in days, does not count with hour, minute and second. So 02/01/2026 is after than 01/01/2026
    //If ever I used LocalDateTime or Instant, 01/01/2026-12:00:01 is after than 01/01/2026-12:00:00 (one second of difference)
    public boolean checkIfCanTransferTodayAndUpdateStatus () {
        if (this.isLocked) {
            if (LocalDate.now().isAfter(lockedAt)) {
                this.isLocked = false;
                this.totalValueTransferredToday = 0.0;
                this.lockedAt = null;
                return true;
            }
            return false;
        }
        return true;
    }
}
