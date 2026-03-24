package api.dto.event_dto;

import java.util.UUID;

public record AuthUserStatusEventDTO

        (String status,

         String errorCause,

         UUID clientCode) {}
