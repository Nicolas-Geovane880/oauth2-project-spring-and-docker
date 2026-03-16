package api.dto.event_dto;

import java.time.LocalDate;

public record UserCreatedEvent
        (Long authUserId,
         String cpf,
         String name,
         String lastName,
         String phone,
         String email,
         LocalDate birthDate) {}
