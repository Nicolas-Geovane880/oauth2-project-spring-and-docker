package api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table (name = "outbox")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Outbox {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "code", nullable = false, unique = true, updatable = false)
    @JdbcTypeCode (SqlTypes.VARCHAR)
    private UUID code;

    @Column (columnDefinition = "json", name = "payload", nullable = false)
    private String payload;

    @CreationTimestamp
    @Column (name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column (name = "is_processed", nullable = false)
    private boolean isProcessed;

    public Outbox(String payload) {
        this.payload = payload;
        this.code = UUID.randomUUID();
        this.isProcessed = false;
    }
}
