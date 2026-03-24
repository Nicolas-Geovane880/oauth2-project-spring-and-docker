package oauth2.dto.events_dto;

import java.util.UUID;

public record AuthUserStatusEventDTO

        (String status,

         String errorCause,

         UUID clientCode) {}
