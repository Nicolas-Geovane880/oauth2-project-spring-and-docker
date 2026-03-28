package oauth2.dto.events_dto;

import oauth2.enums.EventType;

import java.util.UUID;

public record AuthUserEventDTO

        (UUID clientCode,

         String cpf,

         String password) {}
