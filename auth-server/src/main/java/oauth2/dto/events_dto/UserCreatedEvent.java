package oauth2.dto.events_dto;

import lombok.Builder;
import java.time.LocalDate;

@Builder
public record UserCreatedEvent
        (Long authUserId,
         String cpf,
         String name,
         String lastName,
         String phone,
         String email,
         LocalDate birthDate) {}
