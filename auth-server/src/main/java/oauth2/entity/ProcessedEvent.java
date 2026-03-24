package oauth2.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table (name = "processed_event")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessedEvent {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "code", nullable = false, unique = true, updatable = false)
    @JdbcTypeCode (SqlTypes.VARCHAR)
    private UUID code;

    @Column (name = "event_type", nullable = false)
    private String eventType;

    @CreationTimestamp
    @Column (name = "processed_at", nullable = false)
    private LocalDateTime processedAt;

    public ProcessedEvent(String eventType, UUID outboxCode) {
        this.eventType = eventType;
        this.code = outboxCode;
    }
}
