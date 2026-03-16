package gateway.dto.event;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class EventWrapper<T> {

    private UUID uuid;

    private EventType eventType;

    private String version;

    private LocalDateTime timestamp;

    private T payload;
}
