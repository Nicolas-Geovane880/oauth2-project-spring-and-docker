package api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
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

    @Column (name = "last_transfer_date")
    private LocalDate lastTransferDate;

    @Builder.Default
    @Column (name = "total_value_transferred_today", nullable = false)
    private BigDecimal totalValueTransferredToday = new BigDecimal("0.0");

    //I want to limit the transferred values per day
    //LocalDate works in days, does not count with hour, minute and second. So 02/01/2026 is after than 01/01/2026
    //If ever I used LocalDateTime or Instant, 01/01/2026-12:00:01 is after than 01/01/2026-12:00:00 (one second of difference)
    public void resetIfIsNewDay () {
        if (lastTransferDate != null && lastTransferDate.isBefore(LocalDate.now())) {
            this.totalValueTransferredToday = BigDecimal.ZERO;
        }

        this.lastTransferDate = LocalDate.now();
    }

    public void increaseValueTransferredToday (BigDecimal value) {
        this.totalValueTransferredToday = this.totalValueTransferredToday.add(value);
    }
}
