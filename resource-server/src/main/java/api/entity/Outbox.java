package api.entity;

import commons.enums.EventType;
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

    @Column (name = "event_type")
    @Enumerated (value = EnumType.STRING)
    private EventType eventType;

    public Outbox(String payload, EventType eventType) {
        this.payload = payload;
        this.eventType = eventType;
        this.code = UUID.randomUUID();
        this.isProcessed = false;
    }
}
