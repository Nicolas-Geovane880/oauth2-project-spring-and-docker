package api.dto.event_dto;

import api.enums.EventType;
import lombok.*;
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
