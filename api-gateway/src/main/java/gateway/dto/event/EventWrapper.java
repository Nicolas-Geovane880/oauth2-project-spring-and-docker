package gateway.dto.event;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class EventWrapper<T> {

    private UUID uuid;

    private EventType eventType;

    private String version;

    private LocalDateTime timestamp;

    private T payload;

    public EventWrapper (EventType eventType, String version, T payload) {
        this.uuid = UUID.randomUUID();
        this.eventType = eventType;
        this.version = version;
        this.timestamp = LocalDateTime.now();
        this.payload = payload;
    }
}
