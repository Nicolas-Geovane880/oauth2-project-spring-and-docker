package api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table (name = "transfer")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transfer {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn (name = "source_client_account_id", referencedColumnName = "id")
    private ClientAccount sourceAccount;

    @ManyToOne
    @JoinColumn (name = "target_client_account_id", referencedColumnName = "id")
    private ClientAccount targetAccount;

    @CreationTimestamp
    @Column (name = "transferred_at", nullable = false)
    private LocalDateTime transferredAt;

    @Column (name = "value", nullable = false)
    private BigDecimal value;
}
