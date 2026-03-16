package gateway.dto;

import java.time.Instant;

public record AuthResponseDTO
        (Long id,

         String cpf,

         Instant createdAt) {
}
