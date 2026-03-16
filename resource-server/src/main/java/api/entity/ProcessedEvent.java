package api.entity;

import api.enums.EventType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table (name = "processed_event")
@EqualsAndHashCode (of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProcessedEvent {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "uuid", nullable = false, unique = true)
    private UUID uuid;

    @CreationTimestamp
    @Column (name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @Enumerated (value = EnumType.STRING)
    @Column (name = "event_type", nullable = false)
    private EventType eventType;
}
