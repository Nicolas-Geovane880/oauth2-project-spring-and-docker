package oauth2.dto.events_dto;

import lombok.*;
import oauth2.enums.EventType;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter @Builder
public class EventWrapper<T> {
    private UUID uuid;

    private EventType eventType;

    private String version;

    private LocalDateTime timestamp;

    private T payload;
}
