package api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

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

    @Column (name = "code", nullable = false, unique = true, updatable = false)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID code;

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

    @PrePersist
    protected void setUUID () {
        this.code = UUID.randomUUID();
    }
}
