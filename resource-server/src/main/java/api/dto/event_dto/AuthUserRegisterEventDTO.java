package api.dto.event_dto;

import java.util.UUID;

public record AuthUserRegisterEventDTO

        (UUID clientCode,

         String cpf,

         String password) {}
