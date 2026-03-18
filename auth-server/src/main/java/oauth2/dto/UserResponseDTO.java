package oauth2.dto;

import lombok.Builder;
import java.time.Instant;

@Builder
public record UserResponseDTO
        (Long id,

         Instant createdAt) {}
