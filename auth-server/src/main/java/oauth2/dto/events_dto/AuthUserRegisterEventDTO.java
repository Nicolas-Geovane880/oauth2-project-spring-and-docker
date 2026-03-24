package oauth2.dto.events_dto;

import java.util.UUID;

public record AuthUserRegisterEventDTO

        (UUID clientCode,

         String cpf,

         String password) {}
