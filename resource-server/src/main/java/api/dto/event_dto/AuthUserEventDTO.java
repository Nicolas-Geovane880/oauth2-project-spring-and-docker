package api.dto.event_dto;

import java.util.UUID;

public record AuthUserEventDTO

        (UUID clientCode,

         String cpf,

         String password) {}
